package com.test.task.app.ui.controllers

import android.os.Bundle
import android.view.View
import com.bluelinelabs.conductor.Controller
import moxy.MvpDelegate
import moxy.MvpDelegateHolder

abstract class MvpController : Controller(), MvpDelegateHolder {

    private var delegate = MvpDelegate(this)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mvpDelegate.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mvpDelegate.onCreate(savedInstanceState)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        mvpDelegate.onCreate()
        mvpDelegate.onAttach()
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        mvpDelegate.onDetach()
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        mvpDelegate.onDestroy()
        mvpDelegate.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mvpDelegate.onDestroy()
    }

    override fun getMvpDelegate(): MvpDelegate<*> = delegate

    protected fun getString(resourceId : Int) : String? =
        view?.context?.getString(resourceId)

}