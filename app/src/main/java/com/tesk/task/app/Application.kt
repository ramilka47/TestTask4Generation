package com.tesk.task.app

import android.app.Application
import androidx.room.Room
import com.tesk.task.app.modules.ModuleApi
import com.tesk.task.app.modules.ModuleApp
import com.tesk.task.app.modules.ModuleBd
import com.tesk.task.providers.api.impl.ApiGitJoke
import com.tesk.task.providers.http.ImplHttpClient
import com.tesk.task.providers.room.AppDatabase

class Application : Application() {

    lateinit var appComponent : ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerApplicationComponent
            .builder()
            .moduleApp(ModuleApp(this))
            .moduleApi(ModuleApi(ApiGitJoke(ImplHttpClient())))
            .moduleBd(ModuleBd(
                Room
                    .databaseBuilder(this, AppDatabase::class.java, "main")
                    .build())).build()
    }

}