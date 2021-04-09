package com.test.task.app.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ModuleApp(private val application: Application) {

    @Provides
    @Singleton
    fun providesApp() : Context = application

}