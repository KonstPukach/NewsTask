package com.pukachkosnt.data.repository

import com.pukachkosnt.data.api.NewsApi
import com.pukachkosnt.data.mapper.mapToEntity
import com.pukachkosnt.domain.models.ArticleEntity
import com.pukachkosnt.domain.repository.BaseRepository
import java.text.SimpleDateFormat
import java.util.*

// Data layer. Repository receives data from a data source (network)

class NewsFetchRepository(private val newsApi: NewsApi) : BaseRepository {
    override suspend fun fetchNewsWithTimeInterval(
        dateStart: Date,
        dateFinish: Date,
        query: String
    ): List<ArticleEntity> {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val response = newsApi.fetchNewsWithTimeIntervalAsync(
            sdf.format(dateStart),
            sdf.format(dateFinish),
            query
        )
        // map Article to ArticleEntity
        return response.body()?.articlesList?.map { it.mapToEntity() } ?: listOf()
    }
}