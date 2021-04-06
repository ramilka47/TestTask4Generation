package com.tesk.task.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tesk.task.providers.git.models.User
import com.tesk.task.providers.git.GitService
import com.tesk.task.providers.git.response.UserResponse
import com.tesk.task.providers.room.AppDatabase
import com.tesk.task.providers.room.models.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONException
import java.lang.Exception
import java.net.UnknownHostException

class ViewModelSearch(private val gitService: GitService,
                                          private val bd : AppDatabase,
                                          private val coroutineIO : CoroutineScope) : ViewModel() {

    private val mutableLiveDataOfListUsers = MutableLiveData<List<User>>()
    private val mutableLiveDataOfLoading = MutableLiveData<Boolean>()
    private val mutableLiveDataOfError = MutableLiveData<Exception>()
    private val mutableLiveDataOfListUsersIsEmpty = MutableLiveData<Boolean>()
    private val mutableLiveDataIsEmptyQuery = MutableLiveData<Boolean>()
    private val mutableLiveDataApiException = MutableLiveData<Boolean>()
    private val mutableLiveDataShowStartMessage = MutableLiveData<Boolean>()

    val liveDataUsers : LiveData<List<User>> = mutableLiveDataOfListUsers
    val liveDataLoading : LiveData<Boolean> = mutableLiveDataOfLoading
    val liveDataError : LiveData<Exception> = mutableLiveDataOfError
    val liveDataIsEmptyList : LiveData<Boolean> = mutableLiveDataOfListUsersIsEmpty
    val liveDataIsEmptyQuery : LiveData<Boolean> = mutableLiveDataIsEmptyQuery
    val liveDataApiException : LiveData<Boolean> = mutableLiveDataApiException
    val liveDataShowStartMessage : LiveData<Boolean> = mutableLiveDataShowStartMessage

    init {
        mutableLiveDataShowStartMessage.postValue(true)
    }

    private var jobOnSearch : Job? = null
    private var jobIsMyAccount : Job? = null

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

    private suspend fun getUsersFromNet(query: String) : List<User> {
        val callUsers = gitService.getUsers(query)
        val response = callUsers.execute().body()
        val users = response?.items
        val trueListUsers = mutableListOf<User>()

        users?.forEach { userResponse ->
            trueListUsers.add(
                    User(
                            userResponse,
                            try {// пусть количество запросов превышено, просто покажем список с 0-левым списком фолловеров
                                getFollower(userResponse) ?: 0
                            } catch (e : JSONException) {
                                0
                            }))
        }

        return trueListUsers
    }

    private suspend fun getUsersFromBase(query: String) : List<User>{
        val usersEntity = bd.usersDao().getByQuery(query)
        val trueUsers = mutableListOf<User>()

        usersEntity.forEach {entity->
            trueUsers.add(User(entity))
        }

        return trueUsers
    }

    private suspend fun addIntoBase(list : List<User>, query: String){
        val dao = bd.usersDao()
        list.forEach {user->
            val userEntity = UserEntity(user.id, user.name, user.avatar, user.followers, query)
            if (dao.getById(user.id) != null){
                dao.update(userEntity)
            } else
                dao.insert(userEntity)
        }
    }

    private suspend fun getFollower(userResponse: UserResponse) : Int? = gitService.getFollowers(userResponse.login).execute().body()?.size

}