package com.tesk.task.app.viewmodels

import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.api.impl.models.Hub
import com.tesk.task.providers.api.impl.models.User
import com.tesk.task.providers.room.AppDatabase
import com.tesk.task.providers.room.models.RepositoryEntity
import com.tesk.task.providers.room.models.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception

// типа единственный источник данных
class Repository(private val bd : AppDatabase, private val api : IApiGitJoke) {

    private val coroutine = CoroutineScope(Dispatchers.IO)
    private var job : Job? = null

    fun <T, Q> get(iListener: IListener<T>, query : Q){
        job = coroutine.launch {
            try{
                when(query){
                    is String->{
                        val list = getFromNet(query)
                        addIntoBase(query, list)
                        iListener.onSuccess(list as List<T>)
                    }
                    is User->{
                        val list = getFromNet(query)
                        addIntoBase(query, list)
                        iListener.onSuccess(list as List<T>)
                    }
                }
            }catch (e : IOException) {
                val list = when (query) {
                    is String -> {
                        getFromBd(query)
                    }
                    is User -> {
                        getFromBd(query)
                    }
                    else -> {
                        null
                    }
                }
                if (list.isNullOrEmpty()){
                    iListener.onError(e)
                } else {
                    iListener.onSuccess(list as List<T>)
                }
            }
            catch (e : Exception){
                iListener.onError(e)
            }
        }
    }

    fun cancel(){
        job?.cancel()
    }

    private suspend fun addIntoBase(query: String, users : List<User>) = users.forEach { user ->
        val userDao = bd.usersDao()
        val userEnt = userDao.getById(user.id)
        val trueEntity = UserEntity(
                user.id,
                user.name,
                user.avatar,
                user.followers,
                query)

        if (userEnt == null)
            userDao.insert(trueEntity)
        else {
            userDao.update(trueEntity)
        }
    }

    private suspend fun addIntoBase(user: User, list : List<Hub>) = list.forEach{ repo->
        val repositoryDao = bd.hubDao()
        val entity = repositoryDao.getById(repo.id)
        val trueEntity = RepositoryEntity(repo.id,
                repo.name,
                repo.desctiption,
                repo.lastCommit.toString(),
                repo.currentFork,
                repo.countOfFork,
                repo.rating,
                repo.language,
                user.name)

        if (entity != null){
            repositoryDao.update(trueEntity)
        } else {
            repositoryDao.insert(trueEntity)
        }
    }

    private suspend fun getFromBd(query: String) : List<User>{
        val entities = bd.usersDao().getByQuery(query)
        val list = mutableListOf<User>()
        entities.forEach {entity->
            list.add(User(entity))
        }
        return list
    }

    private suspend fun getFromBd(user: User) : List<Hub>{
        val repoEntList = bd.hubDao().getAllByUserName(user.name)
        val repositories = mutableListOf<Hub>()
        repoEntList.forEach {entity ->
            repositories.add(Hub(entity))
        }

        return repositories
    }

    private suspend fun getFromNet(query: String) : List<User> = api.getUsersByQuery(query)

    private suspend fun getFromNet(user: User) : List<Hub> = api.getHubsForUser(user)

    interface IListener<T>{

        fun onSuccess(list : List<T>?)

        fun onError(e : Exception)

    }
}