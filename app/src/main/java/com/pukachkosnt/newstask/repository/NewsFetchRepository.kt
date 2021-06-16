package com.pukachkosnt.newstask.repository

import com.pukachkosnt.newstask.api.NewsApi
import com.pukachkosnt.newstask.models.News
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private const val TAG = "NewsFetchRepository"

// Data layer. Repository receives data from a data source

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