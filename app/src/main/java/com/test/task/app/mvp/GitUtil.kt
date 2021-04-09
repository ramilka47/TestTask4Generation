package com.test.task.app.mvp

object GitUtil {
    const val URL_ACCESS_TOKEN_GIT = "https://github.com/login/oauth/access_token"
    val PATHS_LOGIN: Array<String> = arrayOf("login", "oauth", "authorize")
    const val SCHEME_LOGIN = "https"
    const val HOST_LOGIN = "github.com"
    const val CLIENT_ID = "client_id"
    const val SCOPE = "scope"
    const val USER_EMAIL = "user:email"
}