package com.pukachkosnt.data.models

import com.squareup.moshi.Json

// data layer

data class ArticleApiModel (
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "urlToImage") val urlToImage: String?,
    @Json(name = "publishedAt") val publishedAt: String,
    @Json(name = "source") val source: SourceApiModel,
    @Json(name = "url") val url: String
)