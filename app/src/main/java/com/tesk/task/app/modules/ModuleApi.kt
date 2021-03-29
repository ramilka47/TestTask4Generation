package com.tesk.task.app.modules

import com.google.gson.Gson
import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.api.IHttpClient
import com.tesk.task.providers.api.impl.ApiGitJoke
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = arrayOf(ModuleIHttpClient::class))
class ModuleApi {

    @Provides
    @Singleton
    fun providesApi(iHttpClient: IHttpClient, gson : Gson) : IApiGitJoke = ApiGitJoke(iHttpClient, gson)
}