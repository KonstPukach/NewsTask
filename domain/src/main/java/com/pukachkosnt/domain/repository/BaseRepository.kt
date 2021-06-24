package com.pukachkosnt.domain.repository

import com.pukachkosnt.domain.models.ArticleEntity
import java.util.*

interface BaseRepository {
    suspend fun fetchNewsWithTimeInterval(
        dateStart: Date,
        dateFinish: Date,
        query: String = ""
    ): List<ArticleEntity>
}