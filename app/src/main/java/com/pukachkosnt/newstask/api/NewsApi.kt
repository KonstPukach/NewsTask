package com.pukachkosnt.newstask.api

import com.pukachkosnt.newstask.models.News
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface NewsApi {
    @GET("v2/everything?domains=3dnews.ru&page=1")
    suspend fun fetchNewsWithTimeIntervalAsync(
        @Query("from") dateFrom: String,
        @Query("to") dateTo: String,
        @Query("q") query: String
    ): Response<News>
}