package com.pukachkosnt.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "articles")
data class ArticleEntity (
    val title: String,
    val description: String,
    val urlToImage: String,
    val publishedAt: Long,
    val sourceName: String,
    val url: String,
    val timeAdded: Long = Date().time,
    @PrimaryKey val id: String = title + publishedAt + url
)