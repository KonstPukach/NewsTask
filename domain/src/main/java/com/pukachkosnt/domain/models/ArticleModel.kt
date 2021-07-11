package com.pukachkosnt.domain.models

import java.util.*

// domain layer

data class ArticleModel (
    val title: String,
    val description: String,
    val urlToImage: String,
    val publishedAt: Date,
    val sourceName: String,
    val url: String,
    var isFavorite: Boolean = false
)