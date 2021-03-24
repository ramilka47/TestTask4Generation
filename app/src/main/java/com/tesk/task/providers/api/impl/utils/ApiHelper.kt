package com.tesk.task.providers.api.impl.utils

import com.tesk.task.providers.api.impl.models.User

object ApiHelper {
    private val SEARCH = "search"
    private val USERS = "users"
    private val REPOS = "repos"
    private val FOLLOWERS = "followers"

    private val QUERY = "q"

    fun createParamsSearch(query : String) = mapOf(QUERY to (query))

    fun createPathsRepos(user : User) = arrayOf(USERS, user.name, REPOS)

    fun createPathsSerach() = arrayOf(SEARCH, USERS)

    fun createPathsFollowers(user : User) = arrayOf(USERS, user.name, FOLLOWERS)

}