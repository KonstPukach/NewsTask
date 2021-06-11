package com.pukachkosnt.newstask.models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

class News {
    @Json(name = "articles") lateinit var articlesList: List<Article>
    @Json(name = "totalResults") var totalResults: Int = 0
}