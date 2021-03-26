package com.tesk.task.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tesk.task.providers.api.impl.models.Hub
import com.tesk.task.providers.api.impl.models.User
import java.lang.Exception

sealed class AViewModel<T>(private val repository: Repository) : ViewModel(), Repository.IListener<T> {

    private val data = MutableLiveData<List<T>>()
    val dataLiveData : LiveData<List<T>> = data

    private val loading = MutableLiveData<Boolean>()
    val loadingLiveData : LiveData<Boolean> = loading

    private val error = MutableLiveData<Exception>()
    val errorLiveData : LiveData<Exception> = error

    private val isEmptyList = MutableLiveData<Boolean>()
    val isEmptyListLiveData : LiveData<Boolean> = isEmptyList

    protected fun <Q> loadData(t : Q){
        loading.postValue(true)
        repository.get(this, t)
    }

    override fun onError(e: Exception) {
        loading.postValue(false)
        error.postValue(e)
    }

    override fun onSuccess(list: List<T>?) {
        loading.postValue(false)
        if (list.isNullOrEmpty()){
            isEmptyList.postValue(true)
        } else {
            data.postValue(list)
        }
    }

    class SearchViewModel(repository: Repository) : AViewModel<User>(repository){
        fun getUsers(query : String){
            loadData(query)
        }
    }

    class GetHubViewModel(repository: Repository) : AViewModel<Hub>(repository){
        fun getHubs(user : User){
            loadData(user)
        }
    }
}