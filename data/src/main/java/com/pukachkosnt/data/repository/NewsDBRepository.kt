package com.pukachkosnt.data.repository

import androidx.paging.*
import com.pukachkosnt.data.db.NewsDatabase
import com.pukachkosnt.data.mapper.mapEntity
import com.pukachkosnt.data.mapper.mapToModel
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.BaseDBRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    override suspend fun getAllArticlesFlow(): Flow<PagingData<ArticleModel>> {
        return Pager(PagingConfig(PAGE_SIZE)) {
            database.articleDao().getAllArticlesPaging()
        }.flow.map {
            it.map { entity ->
                entity.mapToModel()
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 50
    }
}