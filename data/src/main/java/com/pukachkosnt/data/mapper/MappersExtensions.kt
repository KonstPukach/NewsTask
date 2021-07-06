package com.pukachkosnt.data.mapper

import com.pukachkosnt.data.models.Article
import com.pukachkosnt.domain.models.ArticleModel
import java.text.SimpleDateFormat
import java.util.*

val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
fun Article.mapToEntity(): ArticleModel {
    return ArticleModel(
        title = this.title,
        description = this.description,
        urlToImage = this.urlToImage,
        publishedAt = sdf.parse(this.publishedAt) ?: Date(),
        sourceName = this.source.name,
        url = this.url
    )
}