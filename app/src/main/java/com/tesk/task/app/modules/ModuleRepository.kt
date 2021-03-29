package com.tesk.task.app.modules

import com.tesk.task.app.Repository
import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.room.AppDatabase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module(includes = arrayOf(ModuleApi::class, ModuleBd::class))
class ModuleRepository {

    @Provides
    fun providesRepository(bd : AppDatabase, api : IApiGitJoke, coroutine : CoroutineScope) : Repository = Repository(bd, api, coroutine)
}