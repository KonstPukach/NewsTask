package com.pukachkosnt.data.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class NewsQueryInterceptor : Interceptor {
    companion object {
        private const val API_KEY = "f3841e980fca46f4b5a680b4f79c9d61"
        private const val PAGE_SIZE = "100"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val newUrl: HttpUrl = request.url().newBuilder()
            .addQueryParameter("apiKey", API_KEY)
            .addQueryParameter("pageSize", PAGE_SIZE)
            .build()
        val newRequest = request
            .newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(newRequest)
    }
}