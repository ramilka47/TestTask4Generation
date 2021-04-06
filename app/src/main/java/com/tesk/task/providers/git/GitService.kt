package com.tesk.task.providers.git

import com.tesk.task.providers.git.response.HubResponse
import com.tesk.task.providers.git.response.SearchResponse
import com.tesk.task.providers.git.response.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitService {

    @GET("/search/users")
    fun getUsers(@Query("q") query : String) : Call<SearchResponse>

    @GET("/users/{userId}/repos")
    fun getRepositories(@Path("userId") userId : String) : Call<List<HubResponse>>

    @GET("/users/{userId}/followers")
    fun getFollowers(@Path("userId") userId: String) : Call<List<UserResponse>>

}