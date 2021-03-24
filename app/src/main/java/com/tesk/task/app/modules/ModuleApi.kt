package com.tesk.task.app.modules

import com.tesk.task.providers.api.IApiGitJoke
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ModuleApi(private val apiGitJoke : IApiGitJoke) {

    @Provides
    @Singleton
    fun providesApi() = apiGitJoke
}