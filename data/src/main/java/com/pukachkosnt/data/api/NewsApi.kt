package com.pukachkosnt.data.api

import com.pukachkosnt.data.models.News
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


// Data layer. NewsApi is represented as a data source

interface NewsApi {
    @GET("v2/everything?page=1")    // page = 1 because of payment restriction
    suspend fun fetchNewsWithTimeIntervalAsync(
        @Query("from") dateFrom: String,
        @Query("to") dateTo: String,
        @Query("q") query: String = "",
        @Query("language") lang: String = LANG,
        @Query("domains") domains: String = DOMAIN
    ): Response<News>

    companion object {
        private const val LANG = "ru"
        private const val DOMAIN = "3dnews.ru"
    }
}