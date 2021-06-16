package com.pukachkosnt.newstask.repository

import com.pukachkosnt.newstask.models.News
import retrofit2.Response
import java.util.*

interface BaseRepository {
    suspend fun fetchNewsWithTimeInterval(
        dateStart: Date,
        dateFinish: Date,
        query: String = ""
    ): Response<News>
}