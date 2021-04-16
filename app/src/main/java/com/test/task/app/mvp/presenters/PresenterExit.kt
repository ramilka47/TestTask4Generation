package com.test.task.app.mvp.presenters

import android.content.SharedPreferences
import com.test.task.app.mvp.PreferenceUtil
import com.test.task.app.mvp.views.IExitView
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class PresenterExit constructor(private val sharedPreferences: SharedPreferences) : MvpPresenter<IExitView>() {

    fun logout(){
        val editor = sharedPreferences.edit()

        editor.remove(PreferenceUtil.TOKEN)
        editor.apply()

        viewState.onExitSuccess()
    }

}