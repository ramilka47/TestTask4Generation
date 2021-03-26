package com.tesk.task.app.modules

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
    fun providesApi(iHttpClient: IHttpClient) : IApiGitJoke = ApiGitJoke(iHttpClient)
}