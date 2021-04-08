package com.tesk.task.app.mvp.views

import android.content.Context
import com.tesk.task.providers.git.models.Hub
import com.tesk.task.providers.git.models.User
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface IHubView : MvpView {

    fun inject(injector : (Context)->Unit)

    fun getUser(getUser : (User)->Unit)

    fun showHubs(list : List<Hub>)

    fun showLoading()

    fun showErrorInternetAccess()

    fun showErrorApiRequestRate()

    fun showEmptyHubs()

}