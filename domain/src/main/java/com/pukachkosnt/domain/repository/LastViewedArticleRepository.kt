package com.pukachkosnt.domain.repository

interface LastViewedArticleRepository {
    fun saveLastViewedArticleId(id: String)

    fun getLastViewedArticleId(): String?
}