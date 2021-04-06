package com.tesk.task.app.modules

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class ModuleCoroutineScope {

    @Singleton
    @Provides
    fun providesCoroutineIO() = CoroutineScope(Dispatchers.IO)

}