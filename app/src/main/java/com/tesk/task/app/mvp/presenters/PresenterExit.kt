package com.tesk.task.app.mvp.presenters

import android.content.SharedPreferences
import com.tesk.task.app.Application
import com.tesk.task.app.mvp.PreferenceUtil
import com.tesk.task.app.mvp.views.IExitView
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class PresenterExit : MvpPresenter<IExitView>() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun attachView(view: IExitView?) {
        super.attachView(view)
        view?.let {
            (it.inject {
                (it.applicationContext as Application).appComponent.inject(this)
            })
        }
    }

    fun logout(){
        val editor = sharedPreferences.edit()

        editor.remove(PreferenceUtil.TOKEN)
        editor.apply()

        viewState.onExitSuccess()
    }

}