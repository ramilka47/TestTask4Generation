package com.tesk.task.app.mvp.presenters

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.tesk.task.R
import com.tesk.task.app.Application
import com.tesk.task.app.mvp.PreferenceUtil
import com.tesk.task.app.mvp.views.ISearchView
import com.tesk.task.providers.git.GitService
import com.tesk.task.providers.git.models.User
import com.tesk.task.providers.git.response.UserResponse
import com.tesk.task.providers.room.AppDatabase
import com.tesk.task.providers.room.models.MyFaceEntity
import com.tesk.task.providers.room.models.UserEntity
import kotlinx.coroutines.*
import moxy.InjectViewState
import moxy.MvpPresenter
import okhttp3.HttpUrl
import org.json.JSONException
import java.lang.Exception
import java.net.UnknownHostException
import javax.inject.Inject

@InjectViewState
class PresenterSearch : MvpPresenter<ISearchView>() {

    init {
        Log.d("presenter", "has initializated")
    }

    companion object{
        private val URL_ACCESS_TOKEN_GIT = "https://github.com/login/oauth/access_token"
        private val PATHS_LOGIN = arrayOf("login", "oauth", "authorize")
        private val SCHEME_LOGIN = "https"
        private val HOST_LOGIN = "github.com"
        private val CLIENT_ID = "client_id"
        private val SCOPE = "scope"
        private val USER_EMAIL = "user:email"
    }

    @Inject
    lateinit var bd : AppDatabase
    @Inject
    lateinit var gitService: GitService
    @Inject
    lateinit var coroutineIO : CoroutineScope
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var jobOnSearch : Job? = null
    private var jobGetAccessToken : Job? = null
    private var jobGetMyProfile : Job? = null

    override fun attachView(view: ISearchView?) {
        super.attachView(view)
        view?.let {
            (it.inject {
                (it.applicationContext as Application).appComponent.inject(this)
            })

            it.intent{intent, githubUrl, gitId, gitSecret ->
                if (intent.data?.toString()?.startsWith(githubUrl) == true) {
                    getAccessTokenGitHub(
                            intent,
                            gitId,
                            gitSecret)
                }
            }
        }
        getMyProfile()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineIO.cancel()
    }

    fun search(query :String?){
        if (query.isNullOrEmpty()){
            viewState.showOnEmptyQuery()
            return
        }
        jobOnSearch?.cancel()

        jobOnSearch = coroutineIO.launch {
            try {
                intoMainThread{viewState.showLoading()}
                val users = getUsersFromNet(query)

                if (users.isNullOrEmpty()) {
                    intoMainThread { viewState.showEmptyUsers() }
                    return@launch
                } else {
                    addIntoBase(users, query)
                    intoMainThread { viewState.showUsers(users) }
                }
            } catch (e : UnknownHostException) {
                val users = getUsersFromBase(query)

                if (users.isNullOrEmpty()){
                    intoMainThread { viewState.showErrorInternetAccess() }
                } else {
                    intoMainThread { viewState.showUsers(users) }
                }
            } catch (e : JSONException){
                intoMainThread {  viewState.showErrorApiRequestRate() }
            }
        }
    }

    private suspend fun intoMainThread(customer : ()->Unit) =
        withContext(Dispatchers.Main) { customer() }

    fun getAccessTokenGitHub(intent : Intent, appId: String, clientSecret: String){
        val data = intent.data
        val code = data?.getQueryParameter("code")
        val state = data?.getQueryParameter("state")
        val redirectUri = data?.getQueryParameter("redirect_uri")

        // че-то случилось
        if (code.isNullOrEmpty()){
            return
        }

        jobGetAccessToken?.cancel()
        jobGetAccessToken = coroutineIO.launch {
            try {
                val accessResult = getAccessTokenGitHub(appId, clientSecret, code, redirectUri, state)
                val token = accessResult.access_token

                addAccessTokenIntoPref(token)

                withContext(Dispatchers.Main){
                    intent.data = null
                }

                getMyProfile()
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    fun login(appId : String, startActivity : (Intent)->Unit){
        val params = mapOf(CLIENT_ID to appId, SCOPE to USER_EMAIL)
        val httpUrl : HttpUrl = HttpUrl.Builder().apply {
            scheme(SCHEME_LOGIN)
            host(HOST_LOGIN)
            PATHS_LOGIN.forEach {
                addPathSegment(it)
            }
            params.forEach { t, u ->
                addQueryParameter(t, u)
            }
        }.build()

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(httpUrl.toString()))
        startActivity(intent)
    }

    fun getMyProfile(){
        jobGetMyProfile?.cancel()

        jobGetMyProfile = coroutineIO.launch {
            getMyAccount()
        }
    }

    private suspend fun addAccessTokenIntoPref(token : String) =
        with(sharedPreferences) {
            val tokenPref = this.getString(PreferenceUtil.TOKEN, null)
            with(this.edit()) {
                if (tokenPref.isNullOrEmpty()) {
                    this.remove(PreferenceUtil.TOKEN)
                    this.apply()
                }
                this.putString(PreferenceUtil.TOKEN, token)
                this.apply()
            }
        }

    private suspend fun getMyAccount() {
        val profile = getMyProfileFromBase()
        if (profile.isNullOrEmpty()) {
            val accessToken = getAccessTokenFromPref()
            if (accessToken != null) {
                try {
                    val profile = getMyProfileFromNet("token $accessToken")
                    addMyProfileIntoBase(profile.login)
                    intoMainThread { viewState.showMyAccount(profile.login) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    intoMainThread { viewState.hideMyAccount() }
                }
            } else {
                intoMainThread { viewState.hideMyAccount() }
            }
        } else {
            intoMainThread { viewState.showMyAccount(profile)  }
        }
    }

    private suspend fun addMyProfileIntoBase(name : String) =
        with(bd.myFaceDao()) {
            this.getById(0)?.let {
                update(MyFaceEntity(it.id, name))
            }
        }

    private suspend fun getAccessTokenFromPref() : String? =
        with(sharedPreferences) {
            this.getString(PreferenceUtil.TOKEN, null)
        }

    private suspend fun getMyProfileFromBase() : String? =
        with(bd.myFaceDao()) {
            this.getById(0)?.name
        }

    private suspend fun getUsersFromNet(query: String) =
        gitService
            .getUsers(query)
            .items.map {
                User(it).apply {
                    followers = try {
                        getFollower(it)
                    } catch (e: JSONException) {
                        0
                    }
                }
            }

    private suspend fun getUsersFromBase(query: String) =
        with(bd.usersDao()) {
            this.getByQuery(query).map {
                User(it)
            }
        }

    private suspend fun addIntoBase(list : List<User>, query: String) =
        with(bd.usersDao()) {
            list.forEach {
                val userEntity = UserEntity(it.id, it.name, it.avatar, it.followers, query)
                if (this.getById(it.id) != null) {
                    this.update(userEntity)
                } else
                    this.insert(userEntity)
            }
        }

    private suspend fun getMyProfileFromNet(accessToken : String) = gitService.myProfile(accessToken)

    private suspend fun getAccessTokenGitHub(appId: String,
                                             clientSecret: String,
                                             code : String,
                                             redirectUri : String?, state : String?) =
        gitService.accessToken(
            URL_ACCESS_TOKEN_GIT,
            appId,
            clientSecret,
            code,
            redirectUri,
            state
        )

    private suspend fun getFollower(userResponse: UserResponse) : Int =
        gitService
            .getFollowers(userResponse.login)
            .size
}