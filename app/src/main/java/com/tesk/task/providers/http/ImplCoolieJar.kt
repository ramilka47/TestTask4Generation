package com.tesk.task.providers.http

import com.google.gson.Gson
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

class ImplCoolieJar : CookieJar, ICookieProvider {

    private val mServerCookieStore = ConcurrentHashMap<String, ArrayList<Cookie>>()

    private val mClientCookieStore = ConcurrentHashMap<String, ArrayList<Cookie>>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> {

        val host = url.host

        var serverCookieList: MutableList<Cookie>? = null

        for (i in mServerCookieStore.keys) {
            if (host.endsWith(i)) {
                serverCookieList = mServerCookieStore[i]
            }
        }

        if (serverCookieList == null) {
            serverCookieList = ArrayList()
        }

        val clientCookieStore = mClientCookieStore[url.host]

        if (clientCookieStore != null) {
            serverCookieList.addAll(clientCookieStore)
        }

        return serverCookieList ?: ArrayList()
    }

    data class Obj(var host : String? = null, val list : MutableList<String> = mutableListOf())

    override fun getLastCookie() : Obj {
        var obj = Obj()
        if (!mServerCookieStore.isNullOrEmpty()) {
            val k = mServerCookieStore.keys.last()
            obj.host = k
            val g = Gson()
            for (a in 0 until mServerCookieStore.get(k)!!.size){
                obj.list.add(g.toJson(mServerCookieStore[k]!![a]))
            }
        }
        return obj
    }

    override fun saveCookie(url: HttpUrl, unmodifiableCookieList: List<Cookie>){
        for (c in unmodifiableCookieList) {
            val str = c.domain
            var list: MutableList<Cookie>? = mServerCookieStore[str]
            if (list == null) {
                list = ArrayList()
                mServerCookieStore[str] = list
            }
            list.add(c)
        }
    }

    override fun saveFromResponse(url: HttpUrl, unmodifiableCookieList: List<Cookie>) {
        saveCookie(url, unmodifiableCookieList)
    }

}
