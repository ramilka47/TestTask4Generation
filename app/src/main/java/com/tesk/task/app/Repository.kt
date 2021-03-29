package com.tesk.task.app

import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.api.impl.models.Hub
import com.tesk.task.providers.api.impl.models.User
import com.tesk.task.providers.room.AppDatabase
import com.tesk.task.providers.room.models.MyFaceEntity
import com.tesk.task.providers.room.models.RepositoryEntity
import com.tesk.task.providers.room.models.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class Repository @Inject constructor(private val bd : AppDatabase, private val api : IApiGitJoke, private val coroutine : CoroutineScope) {

    private var jobGet : Job? = null
    private var jobLogin : Job? = null
    private var jobLogout : Job? = null
    private var jobFollowers : Job? = null
    private var jobMyFace : Job? = null

    fun <R> getMyFace(iListener: IListener<R>){
        jobMyFace = coroutine.launch {
            val myFace = bd.myPageDao().getById(0)
            if (myFace != null){
                iListener.onSuccess(myFace.name as R)
            }
        }
    }

    fun <R> getFollowers(user : User, iListener: IListener<R>){
        jobFollowers = coroutine.launch {
            try{
                val followers = api.getFollowers(user)
                user.followers = followers
                iListener.onSuccess(user as R)
            }catch (e : Exception){
                iListener.onError(e)
            }
        }
    }

    fun <R> logout(iListener: IListener<R>){
        jobLogout = coroutine.launch {
            try {
                api.logOut()
                val myFace = bd.myPageDao().getById(0)
                if (myFace != null){
                    bd.myPageDao().delete(myFace)
                }
                iListener.onSuccess(true as R)
            }catch (e : Exception){
                iListener.onError(e)
            }
        }
    }

    fun <R> auth(login : String, password : String, iListener: IListener<R>){
        jobLogin = coroutine.launch {
            try {
                val res = getFromNet(login, password)
                if (res.length < 4) {
                    throw InvalidException()
                    // Ошибка авторизации (лень было парсить или искать шаблон, взял самый просто)
                } else {
                    addIntoBase(res)
                }
            }catch (e : InvalidException){
                iListener.onError(e)
            }
            catch (e : Exception){
                val res = getFromBd()
                if (res != null){
                    iListener.onSuccess(res as R)
                } else {
                    iListener.onError(e)
                }
            }
        }
    }

    fun <R, Q> get(iListener: IListener<R>, query : Q){
        jobGet = coroutine.launch {
            try{
                when(query){
                    is String->{
                        if (query.isEmpty()){
                            throw DoseNotValidFormatDataException()
                        }
                        val list = getFromNet(query)
                        addIntoBase(query, list)
                        iListener.onSuccess(list as R)
                    }
                    is User->{
                        val list = getFromNet(query)
                        addIntoBase(query, list)
                        iListener.onSuccess(list as R)
                    }
                }
            }catch (e : DoseNotValidFormatDataException){
              iListener.onError(e)
            } catch (e : IOException) {
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
                    iListener.onSuccess(list as R)
                }
            }
            catch (e : Exception){
                iListener.onError(e)
            }
        }
    }

    fun cancel(){
        jobGet?.cancel()
        jobLogin?.cancel()
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

    private suspend fun addIntoBase(name : String){
        bd.myPageDao().insert(MyFaceEntity(0, name))
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

    private suspend fun getFromBd() : String = bd.myPageDao().getById(0).name

    private suspend fun getFromNet(query: String) : List<User> = api.getUsersByQuery(query)

    private suspend fun getFromNet(user: User) : List<Hub> = api.getHubsForUser(user)

    private suspend fun getFromNet(login :String, password: String) : String = api.authorize(login, password)

    interface IListener<T>{

        fun onSuccess(succ : T?)

        fun onError(e : Exception)

    }

    class InvalidException : Exception("login or password is invalid")

    class DoseNotValidFormatDataException : Exception("dose not valid format data")
}