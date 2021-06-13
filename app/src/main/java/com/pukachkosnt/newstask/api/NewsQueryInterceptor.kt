package com.pukachkosnt.newstask.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val API_KEY = "c8be613a9ae54483b76f9983abcdb548"

class NewsQueryInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val newUrl: HttpUrl = request.url().newBuilder()
            .addQueryParameter("apiKey", API_KEY)
            .addQueryParameter("language", "ru")
            .addQueryParameter("pageSize", "100")
            .build()
        val newRequest = request
            .newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(newRequest)
    }
}