package com.tesk.task.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelMyFace : ViewModel() {

    private val showMyFace = MutableLiveData<String?>()
    val showMyFaceLiveData : LiveData<String?> = showMyFace

    private val hideMyFace = MutableLiveData<Boolean>()
    val hideMyFaceLiveData : LiveData<Boolean> = hideMyFace

    fun showMyFace(name : String){
        showMyFace.postValue(name)
        hideMyFace.postValue(false)
    }

    fun hideMyFace(){
        showMyFace.postValue(null)
        hideMyFace.postValue(true)
    }

}