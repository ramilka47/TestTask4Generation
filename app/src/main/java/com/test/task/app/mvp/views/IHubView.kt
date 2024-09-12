package com.test.task.app.mvp.views

import com.test.task.providers.git.models.Hub
import com.test.task.providers.git.models.User
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface IHubView : MvpView {

    fun getUser(getUser : (User)->Unit)

    fun showHubs(list : List<Hub>)

    fun showLoading()

    fun showErrorInternetAccess()

    fun showErrorApiRequestRate()

    fun showEmptyHubs()

}