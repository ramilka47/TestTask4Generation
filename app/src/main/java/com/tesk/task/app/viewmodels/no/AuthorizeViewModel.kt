package com.tesk.task.app.viewmodels.no

import com.tesk.task.R
import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.room.dao.MyFaceDao
import com.tesk.task.providers.room.models.MyFaceEntity
import java.io.IOException

class AuthorizeViewModel(private val api : IApiGitJoke, private val myPageDao: MyFaceDao) : AViewModel<String, Boolean, Int>() {

    suspend fun auth(login : String, password : String){
        post(loading = true)

        // если не тру todo
        try{
            val res = api.authorize(login, password)
            if (res.length < 4){
                post(error = R.string.login_error)
            } else {
                myPageDao.insert(MyFaceEntity(0, res))
                post(success = res)
            }
        } catch (e : IOException){
            val meFromDao = myPageDao.getById(0)
            if (meFromDao != null){
                post(success = meFromDao.name)
            }
            post(error = R.string.internet_access_worn)
        }
    }
}