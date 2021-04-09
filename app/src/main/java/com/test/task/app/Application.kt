package com.test.task.app

import android.app.Application
import com.test.task.app.components.ApplicationComponent
import com.test.task.app.components.DaggerApplicationComponent
import com.test.task.app.modules.*

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