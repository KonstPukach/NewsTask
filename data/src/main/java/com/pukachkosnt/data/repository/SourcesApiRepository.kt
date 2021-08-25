package com.pukachkosnt.data.repository

import com.pukachkosnt.data.api.NewsApi
import com.pukachkosnt.data.mapper.mapToModel
import com.pukachkosnt.domain.models.SourceModel
import com.pukachkosnt.domain.repository.SourcesRepository
import retrofit2.HttpException
import java.lang.Exception
import java.lang.RuntimeException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class SourcesApiRepository(private val newsApi: NewsApi) : SourcesRepository {
    override suspend fun getSources(): List<SourceModel> {
        return try {
            val response = newsApi.fetchSourcesAsync()
            response.body()?.sources?.map {
                it.mapToModel()
            } ?: listOf()
        } catch (e: Exception) {
            listOf()
        }
    }
}