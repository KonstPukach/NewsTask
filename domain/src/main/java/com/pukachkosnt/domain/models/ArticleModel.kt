package com.pukachkosnt.domain.models

import java.util.*

// domain layer

data class ArticleModel (
    val title: String,
    val description: String,
    val urlToImage: String?,
    val publishedAt: Date,
    val sourceName: String,
    val url: String,
    val isFavorite: Boolean = false,
    val id: String = title + publishedAt.time.toString() + url
)