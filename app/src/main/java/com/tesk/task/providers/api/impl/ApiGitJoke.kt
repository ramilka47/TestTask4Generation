package com.tesk.task.providers.api.impl

import com.google.gson.Gson
import com.tesk.task.providers.api.EndpointGit
import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.api.IHttpClient
import com.tesk.task.providers.api.impl.models.Repository
import com.tesk.task.providers.api.impl.models.User
import com.tesk.task.providers.api.impl.result.HubResult
import com.tesk.task.providers.api.impl.result.SearchResult
import com.tesk.task.providers.api.impl.utils.ApiHelper
import com.tesk.task.providers.api.impl.utils.AuthHelper
import com.tesk.task.providers.http.ImplHttpClient
import org.json.JSONArray

class ApiGitJoke(private var iHttpClient: IHttpClient) : IApiGitJoke {

    override suspend fun authorize(login: String, password: String) = AuthHelper.auth(login, password, iHttpClient)

    override suspend fun getUsersByQuery(query: String?): List<User> =
            fromSearchResult(
                    iHttpClient.get(
                            EndpointGit.API.value,
                            mapOf(),
                            ApiHelper.createParamsSearch(query?:throw SearchQueryException()),
                            ApiHelper.createPathsSerach()))

    override suspend fun getHubsForUser(user: User): List<Repository> =
        fromHubResult(
                iHttpClient.get(
                        EndpointGit.API.value,
                        mapOf(),
                        mapOf(),
                        ApiHelper.createPathsRepos(user)))

    override suspend fun getFollowers(user: User): Int =
        fromFollowerResult(
                iHttpClient.get(
                        EndpointGit.API.value,
                        mapOf(),
                        mapOf(),
                        ApiHelper.createPathsFollowers(user)))

    // не хочу писать выход, просто будет обновление
    override suspend fun logOut(): Boolean {
        iHttpClient = ImplHttpClient()
        return true
    }

    private fun fromSearchResult(string : String) : List<User>{
        val result = Gson().fromJson<SearchResult>(string, SearchResult::class.java)
        val users = mutableListOf<User>()
        // может быть пусто, может быть ошибка

        result.items.forEach {userResult ->
            users.add(User(userResult))
        }

        return users
    }

    private fun fromHubResult(string : String) : List<Repository>{
        val intermediateResultList = JSONArray(string)
        val repositories = mutableListOf<Repository>()

        for (i in 0 until intermediateResultList.length()){
            val result = Gson().fromJson<HubResult>(intermediateResultList[i].toString(), HubResult::class.java)
            repositories.add(Repository(result))
        }

        return repositories
    }

    private fun fromFollowerResult(string : String) = JSONArray(string).length()

}