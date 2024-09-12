package com.test.task.app.mvp.views

import android.content.Intent
import com.test.task.providers.git.models.User
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface ISearchView : MvpView {

    fun intent(intent : (Intent, githubUrl:String, gitId:String, gitSecret:String)->Unit)

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