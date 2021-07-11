package com.pukachkosnt.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val urlToImage: String,
    val publishedAt: Long,
    val sourceName: String,
    val url: String
)