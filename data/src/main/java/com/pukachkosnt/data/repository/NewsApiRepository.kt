package com.pukachkosnt.data.repository

import com.pukachkosnt.data.api.NewsApi
import com.pukachkosnt.data.mapper.mapToModel
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.NewsRepository
import java.text.SimpleDateFormat
import java.util.*

// Data layer. Repository receives data from a data source (network)

class NewsApiRepository(private val newsApi: NewsApi) : NewsRepository {
    override suspend fun fetchNewsWithTimeInterval(
        dateStart: Date,
        dateFinish: Date,
        query: String
    ): List<ArticleModel> {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
        val response = newsApi.fetchNewsWithTimeIntervalAsync(
            sdf.format(dateStart),
            sdf.format(dateFinish),
            query
        )
        // map Article to ArticleEntity
        return response.body()?.articlesList?.map { it.mapToModel() } ?: listOf()
    }
}