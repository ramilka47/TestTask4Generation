package com.tesk.task.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tesk.task.app.Repository
import java.lang.Exception

class ViewModelAuthorize(private val repository: Repository) : ViewModel(), Repository.IListener<String>{

    private val error = MutableLiveData<Exception>()
    val errorLiveData : LiveData<Exception> = error

    private val loading = MutableLiveData<Boolean>()
    val loadingLiveData : LiveData<Boolean> = loading

    private val success = MutableLiveData<String>()
    val successLiveData : LiveData<String> = success

    fun auth(login : String, password : String){
        loading.postValue(true)
        repository.auth(login, password, this)
    }

    override fun onError(e: Exception) {
        loading.postValue(false)
        error.postValue(e)
    }

    override fun onSuccess(succ: String?) {
        loading.postValue(false)
        success.postValue(succ)
    }
}