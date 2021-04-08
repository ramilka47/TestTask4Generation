package com.tesk.task.app.mvp.presenters

import com.tesk.task.app.Application
import com.tesk.task.app.mvp.views.IHubView
import com.tesk.task.providers.git.GitService
import com.tesk.task.providers.git.models.Hub
import com.tesk.task.providers.git.models.User
import com.tesk.task.providers.room.AppDatabase
import com.tesk.task.providers.room.models.RepositoryEntity
import kotlinx.coroutines.*
import moxy.InjectViewState
import moxy.MvpPresenter
import org.json.JSONException
import java.net.UnknownHostException
import javax.inject.Inject

@InjectViewState
class PresenterHub : MvpPresenter<IHubView>() {

    @Inject
    lateinit var bd : AppDatabase
    @Inject
    lateinit var gitService: GitService
    @Inject
    lateinit var coroutineIO : CoroutineScope

    private var jobRepositories: Job? = null

    override fun attachView(view: IHubView?) {
        super.attachView(view)
        view?.let {
            (it.inject {
                (it.applicationContext as Application).appComponent.inject(this)
            })
        }
        view?.getUser {
            getRepositories(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineIO.cancel()
    }

    fun getRepositories(user: User) {
        jobRepositories?.cancel()
        jobRepositories = coroutineIO.launch {
            try {
                intoMainThread{ viewState.showLoading() }
                val repositories = getRepositoriesFromNet(user)
                if (repositories.isNullOrEmpty()) {
                    intoMainThread { viewState.showEmptyHubs() }
                    return@launch
                } else {
                    addIntoBase(repositories, user)
                    intoMainThread { viewState.showHubs(repositories) }
                }
            } catch (e: UnknownHostException) {
                val repositories = getRepositoriesFromBase(user)

                if (repositories.isNullOrEmpty()) {
                    intoMainThread { viewState.showErrorInternetAccess() }
                } else {
                    intoMainThread { viewState.showHubs(repositories) }
                }
            } catch (e: JSONException) {
                intoMainThread { viewState.showErrorApiRequestRate() }
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

    private suspend fun intoMainThread(customer : ()->Unit) =
        withContext(Dispatchers.Main) { customer() }
}