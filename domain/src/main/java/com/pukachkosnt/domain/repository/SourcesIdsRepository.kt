package com.pukachkosnt.domain.repository

interface SourcesIdsRepository {
    fun saveNewsSources(sources: Set<String>)
    fun getNewsSources(): Set<String>
}