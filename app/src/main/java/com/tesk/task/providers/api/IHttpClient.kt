package com.tesk.task.providers.api

interface IHttpClient {

    suspend fun get(url : String, headers : Map<String, String>, params : Map<String, String>, paths : Array<String>) : String

    suspend fun post(url : String, headers : Map<String, String>, data : Map<String, String>, paths : Array<String>) : String

}