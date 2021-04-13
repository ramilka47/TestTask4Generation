package com.test.task.app.mvp.presenters

import com.test.task.app.mvp.views.IHubView
import com.test.task.providers.git.GitService
import com.test.task.providers.git.models.Hub
import com.test.task.providers.git.models.User
import com.test.task.providers.room.AppDatabase
import com.test.task.providers.room.models.RepositoryEntity
import kotlinx.coroutines.*
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope
import org.json.JSONException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@InjectViewState
@Singleton
class PresenterHub @Inject constructor(private val bd : AppDatabase,
                                       private val gitService: GitService) : MvpPresenter<IHubView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.getUser {
            getRepositories(it)
        }
    }

    private fun getRepositories(user: User) {
        presenterScope.launch {
            try {
                viewState.showLoading()
                val repositories = getRepositoriesFromNet(user)
                if (repositories.isNullOrEmpty()) {
                    viewState.showEmptyHubs()
                    return@launch
                } else {
                    addIntoBase(repositories, user)
                    viewState.showHubs(repositories)
                }
            } catch (e: UnknownHostException) {
                val repositories = getRepositoriesFromBase(user)

                if (repositories.isNullOrEmpty()) {
                    viewState.showErrorInternetAccess()
                } else {
                    viewState.showHubs(repositories)
                }
            } catch (e: JSONException) {
                viewState.showErrorApiRequestRate()
            }
        }
    }

    private suspend fun getRepositoriesFromNet(user: User) =
        gitService
            .getRepositories(user.name)
            .map {
                Hub(it)
            }

    private suspend fun getRepositoriesFromBase(user: User): List<Hub> = with(bd.hubDao()) {
        this.getAllByUserName(user.name).map {
            Hub(it)
        }
    }

    private suspend fun addIntoBase(list: List<Hub>, user: User) = with(bd.hubDao()) {
        list.forEach {
            val entity = RepositoryEntity(
                it.id,
                it.name,
                it.desctiption,
                it.lastCommit.toString(),
                it.currentFork,
                it.countOfFork,
                it.rating,
                it.language,
                user.name
            )

            if (this.getById(it.id) != null) {
                this.update(entity)
            } else {
                this.insert(entity)
            }
        }
    }

}