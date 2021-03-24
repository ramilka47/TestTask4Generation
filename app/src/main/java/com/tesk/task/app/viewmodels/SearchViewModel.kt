package com.tesk.task.app.viewmodels

import com.tesk.task.R
import com.tesk.task.app.viewmodels.models.SearchSuccess
import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.api.impl.models.User
import com.tesk.task.providers.room.dao.UserDao
import com.tesk.task.providers.room.models.UserEntity
import org.json.JSONException
import java.io.IOException
import java.lang.NullPointerException

class SearchViewModel(private val userDao : UserDao, private val api : IApiGitJoke) : AViewModel<SearchSuccess, Boolean, Int>(){

    suspend fun search(query : String?){
        post(loading = true)

        try{
            val list = fromNet(query!!)
            if (list.isNotEmpty()){
                addIntoBase(query, list)
                post(success = SearchSuccess(query, list))
            } else {
                post(error = R.string.empty_result_query)
            }
        }
        catch (e : NullPointerException){

            // если запрос пустой, скажем сразу здесь, а не дальше
            post(error = R.string.empty_query)
        }
        catch (e : IOException){

            // если нет доступа в интернет
            val result = fromBd(query!!)

            // если нет доступа в интернет и невозможно восстановить последний результат, сообщить об ошибке сети.
            if (result.isNotEmpty()) {
                post(success = SearchSuccess(query, result))
            }
            post(error = R.string.internet_access_worn)
        } catch (e : JSONException){
            post(error = R.string.count_of_requests_get_a_higher_rate_limit)
        }
    }

    private fun addIntoBase(query: String, users : List<User>) = users.forEach { user ->
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

    private suspend fun fromBd(query: String) : List<User>{
        val entities = userDao.getByQuery(query)
        val list = mutableListOf<User>()
        entities.forEach {entity->
            list.add(User(entity))
        }
        return list
    }

    private suspend fun fromNet(query: String) : List<User> = api.getUsersByQuery(query)
}