package com.tesk.task.app.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ModuleApp(private val application: Application) {

    @Provides
    @Singleton
    fun providesApp() = application
}