package com.pukachkosnt.domain.repository

import com.pukachkosnt.domain.models.ArticleModel
import java.util.*

interface NewsByTimeIntervalRepository {
    suspend fun fetchNewsWithTimeInterval(
        dateStart: Date,
        dateFinish: Date,
        source: Set<String> = setOf("bbc-news"),
        query: String = ""
    ): List<ArticleModel>
}