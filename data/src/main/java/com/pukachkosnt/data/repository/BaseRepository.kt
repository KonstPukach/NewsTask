package com.pukachkosnt.data.repository

import com.pukachkosnt.data.models.News
import retrofit2.Response
import java.util.*

// data layer

interface BaseRepository {
    suspend fun fetchNewsWithTimeInterval(
        dateStart: Date,
        dateFinish: Date,
        query: String = ""
    ): Response<News>
}