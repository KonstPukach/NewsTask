package com.pukachkosnt.data.mapper

import com.pukachkosnt.data.entities.ArticleEntity
import com.pukachkosnt.data.models.ArticleApiModel
import com.pukachkosnt.domain.models.ArticleModel
import java.text.SimpleDateFormat
import java.util.*

val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
fun ArticleApiModel.mapToModel(): ArticleModel {
    return ArticleModel(
        title = this.title,
        description = this.description,
        urlToImage = this.urlToImage,
        publishedAt = sdf.parse(this.publishedAt) ?: Date(),
        sourceName = this.source.name,
        url = this.url
    )
}

fun ArticleModel.mapEntity(): ArticleEntity {
    return ArticleEntity(
        title = this.title,
        description = this.description,
        urlToImage = this.urlToImage,
        publishedAt = this.publishedAt.time,
        sourceName = this.sourceName,
        url = this.url
    )
}

fun ArticleEntity.mapToModel(): ArticleModel {
    return ArticleModel(
        title = this.title,
        description = this.description,
        urlToImage = this.urlToImage,
        publishedAt = Date(this.publishedAt),
        sourceName = this.sourceName,
        url = this.url,
        isFavorite = true   // db articles are favorite by default
    )
}
