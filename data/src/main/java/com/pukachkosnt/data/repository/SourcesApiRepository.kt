package com.pukachkosnt.data.repository

import com.pukachkosnt.data.api.NewsApi
import com.pukachkosnt.data.mapper.mapToModel
import com.pukachkosnt.domain.models.SourceModel
import com.pukachkosnt.domain.repository.SourcesRepository

class SourcesApiRepository(private val newsApi: NewsApi) : SourcesRepository {
    override suspend fun getSources(): List<SourceModel> {
        val response = newsApi.fetchSourcesAsync()
        return response.body()?.sources?.map {
            it.mapToModel()
        } ?: listOf()
    }
}