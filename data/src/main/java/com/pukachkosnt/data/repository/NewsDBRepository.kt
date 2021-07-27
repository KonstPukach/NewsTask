package com.pukachkosnt.data.repository

import com.pukachkosnt.data.db.NewsDatabase
import com.pukachkosnt.data.mapper.mapEntity
import com.pukachkosnt.data.mapper.mapToModel
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.BaseDBRepository
import java.io.IOException


class NewsDBRepository(private val database: NewsDatabase) : BaseDBRepository {
    override suspend fun addArticle(articleModel: ArticleModel): Result<ArticleModel> {
        try {
            database.articleDao().insertArticle(articleModel.mapEntity())
        } catch (e: IOException) {
            return Result.failure(e)
        }
        return Result.success(articleModel)
    }

    override suspend fun deleteArticle(articleModel: ArticleModel): Result<ArticleModel> {
        try {
            database.articleDao().deleteArticle(articleModel.id)
        } catch (e: IOException) {
            return Result.failure(e)
        }
        return Result.success(articleModel)
    }

    override suspend fun getIds(): List<String> {
        return database.articleDao().getIds()
    }

    override suspend fun getRangeFavoriteArticles(begin: Int, end: Int): List<ArticleModel> {
        return database.articleDao().getRangeOfArticles(begin, end).map {
            it.mapToModel()
        }
    }
}

