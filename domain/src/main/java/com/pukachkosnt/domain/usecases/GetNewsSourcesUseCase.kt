package com.pukachkosnt.domain.usecases

import com.pukachkosnt.domain.models.SourceModel
import com.pukachkosnt.domain.repository.SourcesIdsRepository
import com.pukachkosnt.domain.repository.SourcesRepository

class GetNewsSourcesUseCase(
    private val sourcesIdsRepository: SourcesIdsRepository,
    private val sourcesRepository: SourcesRepository
) {
    suspend fun getNewsSources(): List<SourceModel> {
        val sources: List<SourceModel> = sourcesRepository.getSources()
        val sourcesIds: Set<String> = sourcesIdsRepository.getNewsSources()
        val resultList = mutableListOf<SourceModel>()

        sources.forEach {
            resultList.add(
                SourceModel(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    language = it.language,
                    country = it.country,
                    checked = sourcesIds.contains(it.id)
                )
            )
        }
        return resultList
    }
}