package com.pukachkosnt.domain.mapper

import com.pukachkosnt.data.models.Article
import com.pukachkosnt.domain.models.ArticleEntity

object ApiToEntityMapper : BaseMapper<com.pukachkosnt.data.models.Article, ArticleEntity> {
    override fun map(type: com.pukachkosnt.data.models.Article): ArticleEntity {
        return ArticleEntity(
            title = type.title,
            description = type.description,
            urlToImage = type.urlToImage
        )
    }
}