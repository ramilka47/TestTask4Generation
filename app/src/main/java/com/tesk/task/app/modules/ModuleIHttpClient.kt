package com.tesk.task.app.modules

import com.google.gson.Gson
import com.tesk.task.providers.api.IHttpClient
import com.tesk.task.providers.http.ImplHttpClient
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class ModuleIHttpClient {

    @Provides
    @Singleton
    fun providesIHttpClient() : IHttpClient = ImplHttpClient()

    @Provides
    @Singleton
    fun providesGson() : Gson = Gson()

    @Provides
    @Singleton
    fun providesCoroutine() = CoroutineScope(Dispatchers.IO)
}