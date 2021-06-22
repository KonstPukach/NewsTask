package com.pukachkosnt.data.models

import com.squareup.moshi.Json

// data layer

data class News (
    @Json(name = "articles") val articlesList: List<Article>,
    @Json(name = "totalResults") val totalResults: Int
)