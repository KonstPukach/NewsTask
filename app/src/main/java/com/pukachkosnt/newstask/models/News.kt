package com.pukachkosnt.newstask.models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class News (
    @Json(name = "articles") val articlesList: List<Article>,
    @Json(name = "totalResults") val totalResults: Int
)