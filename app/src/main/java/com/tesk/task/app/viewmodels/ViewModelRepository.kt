package com.tesk.task.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tesk.task.providers.git.GitService
import com.tesk.task.providers.git.models.Hub
import com.tesk.task.providers.git.models.User
import com.tesk.task.providers.room.AppDatabase
import com.tesk.task.providers.room.models.RepositoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONException
import java.lang.Exception
import java.net.UnknownHostException

class ViewModelRepository(private val gitService: GitService,
                          private val bd : AppDatabase,
                          private val coroutineIO : CoroutineScope) : ViewModel() {

    private val mutableLiveDataRepositories = MutableLiveData<List<Hub>>()
    private val mutableLiveDataLoading = MutableLiveData<Boolean>()
    private val mutableLiveDataError = MutableLiveData<Exception>()
    private val mutableLiveDataOnEmptyList = MutableLiveData<Boolean>()
    private val mutableLiveDataApiException = MutableLiveData<Boolean>()

    val liveDataRepositories: LiveData<List<Hub>> = mutableLiveDataRepositories
    val liveDataLoading: LiveData<Boolean> = mutableLiveDataLoading
    val liveDataError: LiveData<Exception> = mutableLiveDataError
    val liveDataOnEmptyList: LiveData<Boolean> = mutableLiveDataOnEmptyList
    val liveDataApiException: LiveData<Boolean> = mutableLiveDataApiException

    private var jobRepositories: Job? = null

    fun getRepositories(user: User) {
        jobRepositories?.cancel()
        jobRepositories = coroutineIO.launch {
            try {
                mutableLiveDataLoading.postValue(true)
                val repositories = getRepositoriesFromNet(user)
                if (repositories.isNullOrEmpty()) {
                    mutableLiveDataOnEmptyList.postValue(true)
                    return@launch
                } else {
                    addIntoBase(repositories, user)
                    mutableLiveDataRepositories.postValue(repositories)
                }
            } catch (e: UnknownHostException) {
                val repositories = getRepositoriesFromBase(user)

                if (repositories.isNullOrEmpty()) {
                    mutableLiveDataError.postValue(e)
                } else {
                    mutableLiveDataRepositories.postValue(repositories)
                }
            } catch (e: JSONException) {
                mutableLiveDataApiException.postValue(true)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineIO.cancel()
    }

    private suspend fun getRepositoriesFromNet(user: User) =
            gitService
                    .getRepositories(user.name)
                    .map {
                        Hub(it)
                    }

    private suspend fun getRepositoriesFromBase(user: User): List<Hub> = with(bd.hubDao()) {
        this.getAllByUserName(user.name).map {
            Hub(it)
        }
    }

    private suspend fun addIntoBase(list: List<Hub>, user: User) = with(bd.hubDao()) {
        list.forEach {
            val entity = RepositoryEntity(
                    it.id,
                    it.name,
                    it.desctiption,
                    it.lastCommit.toString(),
                    it.currentFork,
                    it.countOfFork,
                    it.rating,
                    it.language,
                    user.name
            )

            if (this.getById(it.id) != null) {
                this.update(entity)
            } else {
                this.insert(entity)
            }
        }
    }
}