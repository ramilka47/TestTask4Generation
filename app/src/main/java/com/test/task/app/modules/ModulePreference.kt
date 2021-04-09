package com.test.task.app.modules

import android.content.Context
import android.content.SharedPreferences
import com.test.task.app.mvp.PreferenceUtil
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ModuleApp::class])
class ModulePreference {

    @Singleton
    @Provides
    fun providesPreference(context: Context): SharedPreferences =
            context
                    .getSharedPreferences(
                            PreferenceUtil.APP_GIT_PREFERENCE,
                            Context.MODE_PRIVATE)

}