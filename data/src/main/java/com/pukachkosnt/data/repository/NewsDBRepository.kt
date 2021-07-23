package com.pukachkosnt.data.repository

import com.pukachkosnt.data.db.NewsDatabase
import com.pukachkosnt.data.mapper.mapEntity
import com.pukachkosnt.data.mapper.mapToModel
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.BaseDBRepository


class NewsDBRepository(private val database: NewsDatabase) : BaseDBRepository {
    override suspend fun addArticle(articleModel: ArticleModel) {
        database.articleDao().insertArticle(articleModel.mapEntity())
    }

    override suspend fun deleteArticle(articleModel: ArticleModel) {
        database.articleDao().deleteArticle(articleModel.publishedAt.time)
    }

    override suspend fun getTimesPublished(): List<Long> {
        return database.articleDao().getTimesPublished()
    }

    override suspend fun getRangeFavoriteArticles(begin: Int, end: Int): List<ArticleModel> {
        return database.articleDao().getRangeOfArticles(begin, end).map {
            it.mapToModel()
        }
    }
}