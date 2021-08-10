package com.pukachkosnt.domain.repository

import com.pukachkosnt.domain.models.ArticleModel

interface NewsByAmountRepository {
    suspend fun getLastArticles(amount: Int): List<ArticleModel>
}