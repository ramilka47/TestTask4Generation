package com.tesk.task.providers.git

import com.tesk.task.providers.git.response.AccessTokenResponse
import com.tesk.task.providers.git.response.HubResponse
import com.tesk.task.providers.git.response.SearchResponse
import com.tesk.task.providers.git.response.UserResponse
import retrofit2.http.*

interface GitService {

    @GET("/search/users")
    suspend fun getUsers(@Query("q") query : String) : SearchResponse

    @GET("/users/{userId}/repos")
    suspend fun getRepositories(@Path("userId") userId : String) : List<HubResponse>

    @GET("/users/{userId}/followers")
    suspend fun getFollowers(@Path("userId") userId: String) : List<UserResponse>

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST
    suspend fun accessToken(
        @Url url : String,
        @Field("client_id") clientId : String,
        @Field("client_secret") clientSecret : String,
        @Field("code") code : String,
        @Field("redirect_uri") redirectUri : String?,
        @Field("state") state : String?) : AccessTokenResponse

    @GET("/user")
    suspend fun myProfile(@Query("access_token") accessToken : String) : UserResponse

}