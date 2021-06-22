package com.pukachkosnt.data.repository

import com.pukachkosnt.data.api.NewsApi
import com.pukachkosnt.data.models.News
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "NewsFetchRepository"

// Data layer. Repository receives data from a data source (network)

class NewsFetchRepository(private val newsApi: NewsApi) : BaseRepository {
    override suspend fun fetchNewsWithTimeInterval(
        dateStart: Date,
        dateFinish: Date,
        query: String
    ): Response<News> {

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return newsApi.fetchNewsWithTimeIntervalAsync(
            sdf.format(dateStart),
            sdf.format(dateFinish),
            query
        )
    }
}