package com.tesk.task.providers.api

import com.tesk.task.providers.api.impl.models.Repository
import com.tesk.task.providers.api.impl.models.User

interface IApiGitJoke {

    suspend fun authorize(login : String, password : String) : String

    suspend fun getUsersByQuery(query : String?) : List<User>

    suspend fun getHubsForUser(user : User) : List<Repository>

    suspend fun getFollowers(user : User) : Int

    suspend fun logOut() : Boolean

}