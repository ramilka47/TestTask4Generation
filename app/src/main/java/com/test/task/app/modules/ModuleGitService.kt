package com.test.task.app.modules

import com.test.task.providers.git.GitService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module (includes = [ModuleRetrofit::class])
class ModuleGitService {

    @Singleton
    @Provides
    fun providesService(retrofit : Retrofit): GitService =
            retrofit
                    .create(GitService::class.java)

}