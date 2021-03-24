package com.tesk.task.app.viewmodels.models

import com.tesk.task.app.viewmodels.AViewModel
import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.room.dao.MyFaceDao

class ViewModelLogOut(private val dao : MyFaceDao, private val api : IApiGitJoke) : AViewModel<Boolean, Boolean, Int>() {

    suspend fun logOut(){
        api.logOut()
        val myFace = dao.getById(0)
        if (myFace != null){
            dao.delete(myFace)
        }
        post(success = true)
    }

}