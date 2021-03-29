package com.tesk.task.app.modules

import com.tesk.task.app.Repository
import com.tesk.task.app.viewmodels.FactoryViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module (includes = arrayOf(ModuleRepository::class))
class ModuleViewModelFactory {

    @Provides
    @Singleton
    fun providesViewModuleFactory(repository: Repository) = FactoryViewModel(repository)
}