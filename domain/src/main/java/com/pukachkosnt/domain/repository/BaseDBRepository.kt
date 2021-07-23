package com.pukachkosnt.domain.repository

import androidx.paging.PagingData
import com.pukachkosnt.domain.models.ArticleModel
import kotlinx.coroutines.flow.Flow

interface BaseDBRepository {
    suspend fun addArticle(articleModel: ArticleModel)

    suspend fun deleteArticle(articleModel: ArticleModel)

    suspend fun getTimesPublished(): List<Long>

    suspend fun getRangeFavoriteArticles(begin: Int, end: Int): List<ArticleModel>
}