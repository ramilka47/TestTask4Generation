package com.tesk.task.providers.http

import com.tesk.task.providers.api.IHttpClient
import okhttp3.*
import java.lang.Exception

class ImplHttpClient : IHttpClient {

    companion object private val CHARSET = "windows-1251"

    val httpClient = OkHttpClient.Builder().
        addInterceptor(UIInterceptor()).
        cookieJar(ImplCoolieJar()).
        build()

    private fun httpUrl(url : String, paths: Array<String>, params: Map<String, String>) : HttpUrl{
        return HttpUrl
            .Builder()
            .scheme("https")
            .apply {
                host(url)
                paths.forEach {
                    addPathSegment(it)
                }
                params.forEach{
                    setQueryParameter(it.key, it.value)
                }
            }
            .build()
    }

    private fun request(httpUrl: HttpUrl, headers: Map<String, String>) : Request.Builder{
       return Request.Builder().apply {
           url(httpUrl)
           headers.forEach {
               header(it.key, it.value)
           }
       }
    }

    private fun get(request: Request.Builder) = request.get().build()

    private fun post(request: Request.Builder, requestBody : RequestBody) = request.post(requestBody).build()

    private fun delete(request: Request.Builder, requestBody: RequestBody? = null) = request.delete(requestBody).build()

    private suspend fun execute(call : Call) : String {
        val response = call.execute()
        val body = response.body
        val bytes = body?.byteStream()?.readBytes() ?: throw HttpException("response body is null")
        return String(bytes)
    }

    override suspend fun get(
        url: String,
        headers: Map<String, String>,
        params: Map<String, String>,
        paths: Array<String>
    ): String {
        return execute(
                httpClient
                    .newCall(
                        get(request(httpUrl(url, paths, params), headers))))

    }

    override suspend fun post(
        url: String,
        headers: Map<String, String>,
        data: Map<String, String>,
        paths: Array<String>
    ): String {
        return execute(
                httpClient
                    .newCall(
                        post(request(httpUrl(url, paths, mapOf()), headers), createFormData(MultipartBody.Builder().setType(MultipartBody.FORM), data))))

    }

    private fun getHttpUrl(url : String, segments : Map<String, String>) : HttpUrl = HttpUrl.Builder().apply {
        scheme("https")
        host(url)
        segments.forEach {
            addQueryParameter(it.key, it.value)
        }
    }.build()

    private fun createFormData(builder : MultipartBody.Builder, map : Map<String, String>) : RequestBody {
        map.forEach{
            builder.addFormDataPart(it.key, it.value)
        }

        return builder.build()
    }

    private class UIInterceptor : Interceptor{

        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val requestWithUserAgent = originalRequest.newBuilder().header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWeb Kit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.143 YaBrowser/19.7.3.172 Yowser/2.5 Safari/537.36"
            ).build()
            val response = chain.proceed(requestWithUserAgent)
            return response
        }

    }

    class HttpException(message : String? = null) : Exception(message)

}