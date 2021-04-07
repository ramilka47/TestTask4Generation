package com.tesk.task.app.modules

import android.content.Context
import com.tesk.task.app.viewmodels.PreferenceUtil
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = arrayOf(ModuleApp::class))
class ModulePreference {

    @Singleton
    @Provides
    fun providesPreference(context: Context) = context.getSharedPreferences(PreferenceUtil.APP_GIT_PREFERENCE, Context.MODE_PRIVATE)

}