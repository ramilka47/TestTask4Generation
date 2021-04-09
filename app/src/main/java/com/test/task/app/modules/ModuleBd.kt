package com.test.task.app.modules

import android.content.Context
import androidx.room.Room
import com.test.task.providers.room.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ModuleApp::class])
class ModuleBd {

    @Provides
    @Singleton
    fun providesBd(context : Context) : AppDatabase =
            Room
                    .databaseBuilder(context, AppDatabase::class.java, "main")
                    .build()
}