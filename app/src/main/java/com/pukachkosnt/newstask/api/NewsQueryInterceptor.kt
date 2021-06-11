package com.pukachkosnt.newstask.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val API_KEY = "f3841e980fca46f4b5a680b4f79c9d61"

class NewsQueryInterceptor(private val searchString: String = "") : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val newUrl: HttpUrl = request.url().newBuilder()
            .addQueryParameter("apiKey", API_KEY)
            .addQueryParameter("language", "ru")
            .addQueryParameter("pageSize", "100")
            .addQueryParameter(
                "q",
            if (searchString.isNotEmpty()) { searchString } else { "" })
            .build()
        val newRequest = request
            .newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(newRequest)
    }
}