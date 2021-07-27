package com.pukachkosnt.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pukachkosnt.data.entities.ArticleEntity

@Dao
interface ArticleDao {
    @Insert
    suspend fun insertArticle(articleEntity: ArticleEntity)

    @Query("DELETE FROM articles WHERE id=:id")
    suspend fun deleteArticle(id: String)

    @Query("SELECT id FROM articles")
    suspend fun getIds(): List<String>

    @Query("SELECT * FROM articles ORDER BY timeAdded DESC LIMIT :begin, :end")
    suspend fun getRangeOfArticles(begin: Int, end: Int): List<ArticleEntity>
}