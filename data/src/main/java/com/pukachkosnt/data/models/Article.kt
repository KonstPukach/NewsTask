package com.pukachkosnt.data.models

import com.squareup.moshi.Json

// data layer

data class Article (
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "urlToImage") val urlToImage: String
)