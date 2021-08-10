package com.pukachkosnt.data.repository

import com.pukachkosnt.data.api.NewsApi
import com.pukachkosnt.data.mapper.mapToModel
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.NewsByAmountRepository
import com.pukachkosnt.domain.repository.NewsByTimeIntervalRepository
import java.text.SimpleDateFormat
import java.util.*

// Data layer. Repository receives data from a data source (network)

class NewsApiRepository(private val newsApi: NewsApi)
    : NewsByTimeIntervalRepository, NewsByAmountRepository {
    override suspend fun fetchNewsWithTimeInterval(
        dateStart: Date,
        dateFinish: Date,
        query: String
    ): List<ArticleModel> {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        val response = newsApi.fetchNewsAsync(
            sdf.format(dateStart),
            sdf.format(dateFinish),
            query
        )
        // map ArticleApiModel to ArticleModel
        return response.body()?.articlesList?.map { it.mapToModel() } ?: listOf()
    }

    override suspend fun getLastArticles(amount: Int): List<ArticleModel> {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        return newsApi.fetchNewsAsync(
            pageSize = amount.toString(),
            dateFrom = sdf.format(
                Calendar.getInstance().apply {
                    add(Calendar.DATE, -1)
                }.time
            )
        ).body()?.articlesList?.map { it.mapToModel() } ?: listOf()
    }
}