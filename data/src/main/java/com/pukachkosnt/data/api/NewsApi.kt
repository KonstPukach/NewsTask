package com.pukachkosnt.data.api

import com.pukachkosnt.data.models.NewsApiModel
import com.pukachkosnt.data.models.SourcesApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


// Data layer. NewsApi is represented as a data source

interface NewsApi {
    @GET("v2/everything?page=1")    // page = 1 because of payment restriction
    suspend fun fetchNewsAsync(
        @Query("from") dateFrom: String = "",
        @Query("to") dateTo: String = "",
        @Query("q") query: String = "",
        @Query("sources") source: String = SOURCE,
        @Query("pageSize") pageSize: String = PAGE_SIZE
    ): Response<NewsApiModel>

    @GET("v2/top-headlines/sources?")
    suspend fun fetchSourcesAsync(): Response<SourcesApiModel>

    companion object {
        private const val SOURCE = "bbc-news"
        private const val PAGE_SIZE = "100"
    }
}