package com.pukachkosnt.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pukachkosnt.data.entities.ArticleEntity

@Dao
interface ArticleDao {
    @Insert
    suspend fun insertArticle(articleEntity: ArticleEntity)

    @Query("DELETE FROM articles WHERE publishedAt=:publishedAt")
    suspend fun deleteArticle(publishedAt: Long)

    @Query("SELECT publishedAt FROM articles")
    suspend fun getTimesPublished(): List<Long>

    @Query("SELECT * FROM articles ORDER BY id DESC LIMIT :begin, :end")
    suspend fun getRangeOfArticles(begin: Int, end: Int): List<ArticleEntity>
}