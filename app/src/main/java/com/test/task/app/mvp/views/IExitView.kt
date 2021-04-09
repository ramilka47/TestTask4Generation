package com.test.task.app.mvp.views

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface IExitView : MvpView {

    fun onExitSuccess()

    fun onExitError()

}