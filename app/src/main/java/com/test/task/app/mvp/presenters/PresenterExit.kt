package com.test.task.app.mvp.presenters

import android.content.SharedPreferences
import com.test.task.app.mvp.PreferenceUtil
import com.test.task.app.mvp.views.IExitView
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject
import javax.inject.Singleton

@InjectViewState
@Singleton
class PresenterExit @Inject constructor(private val sharedPreferences: SharedPreferences) : MvpPresenter<IExitView>() {

    fun logout(){
        val editor = sharedPreferences.edit()

        editor.remove(PreferenceUtil.TOKEN)
        editor.apply()

        viewState.onExitSuccess()
    }

}