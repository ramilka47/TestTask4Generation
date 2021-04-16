package com.test.task.app.mvp.presenters

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.test.task.app.mvp.GitUtil
import com.test.task.app.mvp.PreferenceUtil
import com.test.task.app.mvp.views.ISearchView
import com.test.task.providers.git.GitService
import com.test.task.providers.git.models.User
import com.test.task.providers.git.response.UserResponse
import com.test.task.providers.room.AppDatabase
import com.test.task.providers.room.models.MyFaceEntity
import com.test.task.providers.room.models.UserEntity
import kotlinx.coroutines.*
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.HttpUrl
import org.json.JSONException
import java.lang.Exception
import java.net.UnknownHostException

@InjectViewState
class PresenterSearch constructor(private val bd : AppDatabase,
                                          private val gitService: GitService,
                                          private val sharedPreferences: SharedPreferences) : MvpPresenter<ISearchView>() {

    private var jobOnSearch : Job? = null
    private var jobGetAccessToken : Job? = null
    private var jobGetMyProfile : Job? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.intent { intent, githubUrl, gitId, gitSecret ->
            if (intent.data?.toString()?.startsWith(githubUrl) == true) {
                getAccessTokenGitHub(
                    intent,
                    gitId,
                    gitSecret
                )
            }
        }
        getMyProfile()
        viewState.showStart()
    }

    fun search(query :String?){
        if (query.isNullOrEmpty()){
            viewState.showOnEmptyQuery()
            return
        }
        jobOnSearch?.cancel()

        jobOnSearch =  presenterScope.launch {
            try {
                viewState.showLoading()
                val users = getUsersFromNet(query)

                if (users.isNullOrEmpty()) {
                    viewState.showEmptyUsers()
                    return@launch
                } else {
                    addIntoBase(users, query)
                    viewState.showUsers(users)
                }
            } catch (e : UnknownHostException) {
                val users = getUsersFromBase(query)

                if (users.isNullOrEmpty()){
                    viewState.showErrorInternetAccess()
                } else {
                    viewState.showUsers(users)
                }
            } catch (e : JSONException){
                viewState.showErrorApiRequestRate()
            }
        }
    }

    private fun getAccessTokenGitHub(intent : Intent, appId: String, clientSecret: String) {
        val data = intent.data
        val code = data?.getQueryParameter("code")
        val state = data?.getQueryParameter("state")
        val redirectUri = data?.getQueryParameter("redirect_uri")

        // че-то случилось
        if (code.isNullOrEmpty()) {
            return
        }

        jobGetAccessToken?.cancel()
        jobGetAccessToken = presenterScope.launch {
            try {
                val accessResult = getAccessTokenGitHub(appId, clientSecret, code, redirectUri, state)
                val token = accessResult.access_token

                addAccessTokenIntoPref(token)

                withContext(Dispatchers.Main) {
                    intent.data = null
                }

                getMyProfile()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun login(appId : String, startActivity : (Intent)->Unit){
        val params = mapOf(GitUtil.CLIENT_ID to appId, GitUtil.SCOPE to GitUtil.USER_EMAIL)
        val httpUrl : HttpUrl = HttpUrl.Builder().apply {
            scheme(GitUtil.SCHEME_LOGIN)
            host(GitUtil.HOST_LOGIN)
            GitUtil.PATHS_LOGIN.forEach {
                addPathSegment(it)
            }
            params.forEach { (t, u) ->
                addQueryParameter(t, u)
            }
        }.build()

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(httpUrl.toString()))
        startActivity(intent)
    }

    fun getMyProfile(){
        jobGetMyProfile?.cancel()

        jobGetMyProfile = presenterScope.launch {
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
        withContext(Dispatchers.IO){
            val profile = getMyProfileFromBase()
            if (profile.isNullOrEmpty()) {
                val accessToken = getAccessTokenFromPref()
                if (accessToken != null) {
                    try {
                        val profile = getMyProfileFromNet("token $accessToken")
                        addMyProfileIntoBase(profile.login)
                        withContext(Dispatchers.Main){viewState.showMyAccount(profile.login)}
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main){viewState.hideMyAccount()}
                    }
                } else {
                    withContext(Dispatchers.Main){viewState.hideMyAccount()}
                }
            } else {
                withContext(Dispatchers.Main){viewState.showMyAccount(profile)}
            }
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
            GitUtil.URL_ACCESS_TOKEN_GIT,
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