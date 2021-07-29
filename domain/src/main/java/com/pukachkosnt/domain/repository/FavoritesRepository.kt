package com.pukachkosnt.domain.repository

import com.pukachkosnt.domain.models.ArticleModel

interface FavoritesRepository {
    suspend fun addArticle(articleModel: ArticleModel): Result<ArticleModel>

    suspend fun deleteArticle(articleModel: ArticleModel): Result<ArticleModel>

    suspend fun getIds(): List<String>

    suspend fun getRangeFavoriteArticles(begin: Int, end: Int): List<ArticleModel>
}