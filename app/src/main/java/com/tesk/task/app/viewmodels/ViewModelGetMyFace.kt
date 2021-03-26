package com.tesk.task.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Exception

class ViewModelGetMyFace(private val repository: Repository) : ViewModel(), Repository.IListener<String> {
    private val success = MutableLiveData<String>()
    val successLiveData : LiveData<String> = success

    fun get(){
        repository.getMyFace(this)
    }

    override fun onError(e: Exception) {
        TODO("Not yet implemented")
    }

    override fun onSuccess(succ: String?) {
        success.postValue(succ)
    }
}