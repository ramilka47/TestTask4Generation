package com.tesk.task.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Exception

class ViewModelLogOut(private val repository: Repository) : ViewModel(), Repository.IListener<Boolean> {

    private val exit = MutableLiveData<Boolean>()
    val exitLiveData : LiveData<Boolean> = exit

    private val loading = MutableLiveData<Boolean>()
    val loadingLiveData : LiveData<Boolean> = loading

    private val error = MutableLiveData<Exception>()
    val errorLiveData : LiveData<Exception> = error

    fun logOut(){
        loading.postValue(true)
        repository.logout(this)
    }

    override fun onError(e: Exception) {
        loading.postValue(false)
        error.postValue(e)
    }

    override fun onSuccess(succ: Boolean?) {
        loading.postValue(false)
        exit.postValue(true)
    }
}