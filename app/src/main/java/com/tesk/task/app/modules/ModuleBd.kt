package com.tesk.task.app.modules

import com.tesk.task.providers.room.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ModuleBd(private val bd : AppDatabase) {

    @Provides
    @Singleton
    fun providesBd() = bd
}