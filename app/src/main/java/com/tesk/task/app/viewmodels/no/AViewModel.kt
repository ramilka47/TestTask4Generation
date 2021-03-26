package com.tesk.task.app.viewmodels.no

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

abstract class AViewModel<S, L, E> {
    private val mutableLiveData = MutableLiveData<Triple<S?, L?, E?>>()
    val liveData : LiveData<Triple<S?, L?, E?>> = mutableLiveData

    protected fun post(success: S? = null, loading : L?= null, error : E? = null){
        mutableLiveData.postValue(Triple(success, loading, error))
    }
}