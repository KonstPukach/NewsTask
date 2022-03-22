package com.pukachkosnt.newstask.models.mappers

import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.newstask.models.ArticleUiModel

fun ArticleModel.mapToUiModel(): ArticleUiModel {
    return ArticleUiModel(
        title       = title,
        description = description,
        urlToImage  = urlToImage,
        publishedAt = publishedAt,
        sourceName  = sourceName,
        url         = url,
        isFavorite  = isFavorite,
        id          = id
    )
}

fun ArticleUiModel.mapToDomainModel(): ArticleModel {
    return ArticleModel(
        title       = title,
        description = description,
        urlToImage  = urlToImage,
        publishedAt = publishedAt,
        sourceName  = sourceName,
        url         = url,
        isFavorite  = isFavorite,
        id          = id
    )
}
