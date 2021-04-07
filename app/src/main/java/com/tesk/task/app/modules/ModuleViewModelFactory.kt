package com.tesk.task.app.modules

import android.content.SharedPreferences
import com.tesk.task.app.viewmodels.FactoryViewModel
import com.tesk.task.providers.git.GitService
import com.tesk.task.providers.room.AppDatabase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import retrofit2.Retrofit
import javax.inject.Singleton

@Module (includes = arrayOf(ModuleBd::class, ModuleGitService::class, ModuleCoroutineScope::class, ModulePreference::class))
class ModuleViewModelFactory {

    @Provides
    @Singleton
    fun providesViewModuleFactory(gitService : GitService,
                                  bd : AppDatabase,
                                  coroutineIO : CoroutineScope,
                                  sharedPreferences: SharedPreferences) =
            FactoryViewModel(bd, gitService, coroutineIO, sharedPreferences)
}