package com.tesk.task.app.modules

import com.tesk.task.providers.git.GitService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module (includes = arrayOf(ModuleRetrofit::class))
class ModuleGitService {

    @Singleton
    @Provides
    fun providesService(retrofit : Retrofit) = retrofit.create(GitService::class.java)

}