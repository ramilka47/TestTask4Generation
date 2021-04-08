package com.tesk.task.app

import android.app.Application
import com.tesk.task.app.components.ApplicationComponent
import com.tesk.task.app.components.DaggerApplicationComponent
import com.tesk.task.app.modules.*

class Application : Application() {

    lateinit var appComponent : ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerApplicationComponent
                .builder()
                .moduleApp(ModuleApp(this))
                .build()
    }

}