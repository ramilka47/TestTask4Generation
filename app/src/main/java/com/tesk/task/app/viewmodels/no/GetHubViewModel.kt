package com.tesk.task.app.viewmodels.no

import com.tesk.task.R
import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.api.impl.models.Hub
import com.tesk.task.providers.api.impl.models.User
import com.tesk.task.providers.room.dao.RepositoryDao
import com.tesk.task.providers.room.models.RepositoryEntity
import org.json.JSONException
import java.io.IOException

class GetHubViewModel(private val repositoryDao: RepositoryDao, private val api : IApiGitJoke) : AViewModel<List<Hub>, Boolean, Int>(){

    suspend fun getRepository(user: User){
        post(loading = true)

        try{
            val list = getFromNet(user)

            if (list.isNotEmpty()){
                addIntoBase(user, list)
                post(success = list)
            } else {
                post(error = R.string.worn_query)
            }
        }catch (e : IOException){
            val list = getFromBd(user)
            if (list.isNotEmpty()){
                post(success = list)
            }
            post(error = R.string.internet_access_worn)
        } catch (e : JSONException){
            post(error = R.string.count_of_requests_get_a_higher_rate_limit)
        }
    }

    private suspend fun addIntoBase(user: User, list : List<Hub>) = list.forEach{ repo->
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

    private suspend fun getFromBd(user: User) : List<Hub>{
        val repoEntList = repositoryDao.getAllByUserName(user.name)
        val repositories = mutableListOf<Hub>()
        repoEntList.forEach {entity ->
            repositories.add(Hub(entity))
        }

        return repositories
    }

    private suspend fun getFromNet(user: User) : List<Hub> = api.getHubsForUser(user)

}