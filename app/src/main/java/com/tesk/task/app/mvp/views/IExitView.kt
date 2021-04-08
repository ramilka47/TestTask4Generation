package com.tesk.task.app.mvp.views

import android.content.Context
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface IExitView : MvpView {

    fun inject(injector : (Context)->Unit)

    fun onExitSuccess()

    fun onExitError()

}