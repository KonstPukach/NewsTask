package com.pukachkosnt.newstask.api

import com.pukachkosnt.newstask.models.News
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "NewsFetchRepository"

class NewsFetchRepository(private val newsApi: NewsApi) {

    suspend fun fetchNewsWithTimeInterval(
        dateStart: Date,
        dateFinish: Date,
        query: String = ""
    ): Response<News> {

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return newsApi.fetchNewsWithTimeIntervalAsync(
            sdf.format(dateStart),
            sdf.format(dateFinish),
            query
        )
    }
}