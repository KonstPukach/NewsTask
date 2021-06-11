package com.pukachkosnt.newstask.models

import com.squareup.moshi.Json


class Article {
    @Json(name = "title") var title: String = ""
    @Json(name = "description") var description: String = ""
    @Json(name = "urlToImage") var urlToImage: String = ""
}