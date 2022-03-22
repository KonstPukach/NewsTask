package com.pukachkosnt.domain.repository

import com.pukachkosnt.domain.models.SourceModel

interface SourcesRepository {
    suspend fun getSources(): List<SourceModel>
}