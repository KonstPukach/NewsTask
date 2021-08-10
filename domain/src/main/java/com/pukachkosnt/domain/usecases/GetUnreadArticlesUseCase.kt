package com.pukachkosnt.domain.usecases

import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.LastViewedArticleRepository
import com.pukachkosnt.domain.repository.NewsByAmountRepository

class GetUnreadArticlesUseCase(
    private val newsByAmountRepository: NewsByAmountRepository,
    private val lastViewedArticleRepository: LastViewedArticleRepository,
    private val maxCheckedArticles: Int
) {
    suspend fun getLastUnreadArticles(): List<ArticleModel> {
        val lastViewedArticleId =
            lastViewedArticleRepository.getLastViewedArticleId() ?: return listOf()

        // get the newest article
        val lastPublishedArticles = newsByAmountRepository.getLastArticles(1)

        if (lastPublishedArticles.isNotEmpty()) {
            val lastArticle = lastPublishedArticles.last()
            return if (lastArticle.id == lastViewedArticleId) {
                listOf()
            } else {
                val fetchedNews = newsByAmountRepository.getLastArticles(maxCheckedArticles)
                val index = fetchedNews.indexOfFirst { it.id == lastViewedArticleId }
                if (index < 0)
                    return fetchedNews
                fetchedNews.subList(0, index)
            }
        }
        return listOf()
    }
}