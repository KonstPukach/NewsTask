package com.pukachkosnt.newstask.mapper

import com.pukachkosnt.newstask.models.Article
import com.pukachkosnt.newstask.models.ArticleEntity

object ApiToEntityMapper : BaseMapper<Article, ArticleEntity> {
    override fun map(type: Article): ArticleEntity {
        return ArticleEntity(
            title = type.title,
            description = type.description,
            urlToImage = type.urlToImage
        )
    }
}