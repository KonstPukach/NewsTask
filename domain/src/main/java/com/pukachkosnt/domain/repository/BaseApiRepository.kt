package com.pukachkosnt.domain.repository

import com.pukachkosnt.domain.models.ArticleModel
import java.util.*

interface BaseApiRepository {
    suspend fun fetchNewsWithTimeInterval(
        dateStart: Date,
        dateFinish: Date,
        query: String = ""
    ): List<ArticleModel>
}