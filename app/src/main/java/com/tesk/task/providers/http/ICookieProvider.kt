package com.tesk.task.providers.http

import okhttp3.Cookie
import okhttp3.HttpUrl

interface ICookieProvider {

    fun getLastCookie() : ImplCoolieJar.Obj

    fun saveCookie(url: HttpUrl, unmodifiableCookieList: List<Cookie>)

}