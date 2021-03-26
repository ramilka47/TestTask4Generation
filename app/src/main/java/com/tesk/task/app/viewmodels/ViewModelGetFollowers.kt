package com.tesk.task.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tesk.task.providers.api.impl.models.User

class ViewModelGetFollowers(private val repository: Repository) : ViewModel(), Repository.IListener<Int> {

    private val followers = MutableLiveData<Int>()
    val followersLiveData : LiveData<Int> = followers

    private val loading = MutableLiveData<Boolean>()
    val loadingLiveData : LiveData<Boolean> = loading

    private val error = MutableLiveData<Exception>()
    val errorLiveData : LiveData<Exception> = error

    fun getFollowers(user : User){
        loading.postValue(true)
        repository.getFollowers(user, this)
    }

    override fun onError(e: Exception) {
        loading.postValue(false)
        error.postValue(e)
    }

    override fun onSuccess(succ: Int?) {
        loading.postValue(false)
        followers.postValue(succ)
    }
}