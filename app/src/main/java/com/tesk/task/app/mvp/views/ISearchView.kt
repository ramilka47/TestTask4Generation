package com.tesk.task.app.mvp.views

import android.content.Context
import android.content.Intent
import com.tesk.task.providers.git.models.User
import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface ISearchView : MvpView {

    fun inject(injector : (Context)->Unit)

    fun showMyAccount(name : String)

    fun hideMyAccount()

    fun showUsers(list : List<User>)

    fun showLoading()

    fun showOnEmptyQuery()

    fun showEmptyUsers()

    fun showStart()

    fun showErrorApiRequestRate()

    fun showErrorInternetAccess()

}