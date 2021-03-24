package com.tesk.task.app.viewmodels

import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.api.impl.models.User
import com.tesk.task.providers.room.dao.UserDao
import com.tesk.task.providers.room.models.UserEntity
import org.json.JSONException
import java.io.IOException

object GetFollowersViewModel {

    suspend fun getFollowers(userDao : UserDao, api : IApiGitJoke, user : User) : Int{
        try {
            val followers = api.getFollowers(user)
            val ent = userDao.getById(user.id)
            userDao.update(UserEntity(user.id, user.name, user.avatar, followers, ent.query))

            return followers
        } catch (e : IOException) {
            val ent = userDao.getById(user.id)
            if (ent == null) {
                throw e
            } else {
                return ent.followers
            }
        } catch (e : JSONException){
            return 0
        }
    }

}