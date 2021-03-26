package com.tesk.task.app.modules

import com.tesk.task.app.viewmodels.Repository
import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.room.AppDatabase
import dagger.Module
import dagger.Provides

@Module(includes = arrayOf(ModuleApi::class, ModuleBd::class))
class ModuleRepository {

    @Provides
    fun providesRepository(bd : AppDatabase, api : IApiGitJoke) : Repository = Repository(bd, api)
}