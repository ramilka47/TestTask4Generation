package com.tesk.task.app.viewmodels

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tesk.task.providers.git.models.User
import com.tesk.task.providers.git.GitService
import com.tesk.task.providers.git.response.UserResponse
import com.tesk.task.providers.room.AppDatabase
import com.tesk.task.providers.room.models.MyFaceEntity
import com.tesk.task.providers.room.models.UserEntity
import kotlinx.coroutines.*
import okhttp3.HttpUrl
import org.json.JSONException
import java.lang.Exception
import java.net.UnknownHostException

class ViewModelSearch(private val gitService: GitService,
                      private val bd : AppDatabase,
                      private val coroutineIO : CoroutineScope,
                      private val sharedPreferences: SharedPreferences) : ViewModel() {

    companion object{
        private val URL_ACCESS_TOKEN_GIT = "https://github.com/login/oauth/access_token"
        private val PATHS_LOGIN = arrayOf("login", "oauth", "authorize")
        private val SCHEME_LOGIN = "https"
        private val HOST_LOGIN = "github.com"
        private val CLIENT_ID = "client_id"
        private val SCOPE = "scope"
        private val USER_EMAIL = "user:email"
    }

    private val mutableLiveDataOfListUsers = MutableLiveData<List<User>>()
    private val mutableLiveDataOfLoading = MutableLiveData<Boolean>()
    private val mutableLiveDataOfError = MutableLiveData<Exception>()
    private val mutableLiveDataOfListUsersIsEmpty = MutableLiveData<Boolean>()
    private val mutableLiveDataIsEmptyQuery = MutableLiveData<Boolean>()
    private val mutableLiveDataApiException = MutableLiveData<Boolean>()
    private val mutableLiveDataShowStartMessage = MutableLiveData<Boolean>()
    private val mutableLiveDataShowUser = MutableLiveData<String>()
    private val mutableLiveDataHideUser = MutableLiveData<Boolean>()

    val liveDataUsers : LiveData<List<User>> = mutableLiveDataOfListUsers
    val liveDataLoading : LiveData<Boolean> = mutableLiveDataOfLoading
    val liveDataError : LiveData<Exception> = mutableLiveDataOfError
    val liveDataIsEmptyList : LiveData<Boolean> = mutableLiveDataOfListUsersIsEmpty
    val liveDataIsEmptyQuery : LiveData<Boolean> = mutableLiveDataIsEmptyQuery
    val liveDataApiException : LiveData<Boolean> = mutableLiveDataApiException
    val liveDataShowStartMessage : LiveData<Boolean> = mutableLiveDataShowStartMessage
    val liveDataShowUser : LiveData<String> = mutableLiveDataShowUser
    val liveDataHideUser : LiveData<Boolean> = mutableLiveDataHideUser

    init {
        mutableLiveDataShowStartMessage.postValue(true)
        getMyProfile()
    }

    private var jobOnSearch : Job? = null
    private var jobGetAccessToken : Job? = null
    private var jobGetMyProfile : Job? = null

    fun search(query :String?){
        if (query.isNullOrEmpty()){
            mutableLiveDataIsEmptyQuery.postValue(true)
            return
        }
        jobOnSearch?.cancel()

        jobOnSearch = coroutineIO.launch {
            try {
                mutableLiveDataOfLoading.postValue(true)
                val users = getUsersFromNet(query)

                if (users.isNullOrEmpty()) {
                    mutableLiveDataOfListUsersIsEmpty.postValue(true)
                    return@launch
                } else {
                    addIntoBase(users, query)
                    mutableLiveDataOfListUsers.postValue(users)
                }
            } catch (e : UnknownHostException) {
                val users = getUsersFromBase(query)

                if (users.isNullOrEmpty()){
                    mutableLiveDataOfError.postValue(e)
                } else {
                    mutableLiveDataOfListUsers.postValue(users)
                }
            } catch (e : JSONException){
                mutableLiveDataApiException.postValue(true)
            }
        }
    }

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

    override fun onCleared() {
        super.onCleared()
        coroutineIO.cancel()
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
                    mutableLiveDataShowUser.postValue(profile.login)
                } catch (e: Exception) {
                    e.printStackTrace()
                    mutableLiveDataHideUser.postValue(true)
                }
            } else {
                mutableLiveDataHideUser.postValue(true)
            }
        } else {
            mutableLiveDataShowUser.postValue(profile)
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