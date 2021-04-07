package com.tesk.task.app.viewmodels

import android.content.Context

object PreferenceUtil {

    val APP_GIT_PREFERENCE = "AppGitPreference"
    val TOKEN = "token"

    fun gitPreference(context: Context) = context.getSharedPreferences(APP_GIT_PREFERENCE, Context.MODE_PRIVATE)

}