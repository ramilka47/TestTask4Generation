package com.tesk.task.app.modules

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ModuleRetrofit {

    @Provides
    @Singleton
    fun providesRetrofit() = Retrofit
        .Builder()
        .baseUrl("https://api.github.com")
        .apply {
            addConverterFactory(GsonConverterFactory.create())

        }.build()

}