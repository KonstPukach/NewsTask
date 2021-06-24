package com.pukachkosnt.data.mapper

import com.pukachkosnt.data.models.Article
import com.pukachkosnt.domain.models.ArticleEntity

fun Article.mapToEntity(): ArticleEntity {
    return ArticleEntity(
        title = this.title,
        description = this.description,
        urlToImage = this.urlToImage
    )
}