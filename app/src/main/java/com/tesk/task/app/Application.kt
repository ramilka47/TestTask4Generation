package com.tesk.task.app

import android.app.Application
import androidx.room.Room
import com.tesk.task.app.components.ApplicationComponent
import com.tesk.task.app.components.DaggerApplicationComponent
import com.tesk.task.app.modules.*
import com.tesk.task.providers.room.AppDatabase

class Application : Application() {

    lateinit var appComponent : ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        val bd = Room
                .databaseBuilder(this, AppDatabase::class.java, "main")
                .build()

        appComponent = DaggerApplicationComponent
                .builder()
                .moduleApp(ModuleApp(this))
                .moduleIHttpClient(ModuleIHttpClient())
                .moduleApi(ModuleApi())
                .moduleBd(ModuleBd())
                .moduleViewModelFactory(ModuleViewModelFactory())
                .build()
    }

}