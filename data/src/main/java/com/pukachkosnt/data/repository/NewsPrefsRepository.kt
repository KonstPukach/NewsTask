package com.pukachkosnt.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.pukachkosnt.domain.repository.LastViewedArticleRepository
import com.pukachkosnt.domain.repository.SourcesIdsRepository

class NewsPrefsRepository(
    context: Context,
    prefsPath: String
) : LastViewedArticleRepository, SourcesIdsRepository {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        prefsPath,
        Context.MODE_PRIVATE
    )

    override fun saveLastViewedArticleId(id: String) {
        prefs.edit()
            .putString(PREF_LAST_ARTICLE_ID, id)
            .apply()
    }

    override fun getLastViewedArticleId(): String? {
        return prefs.getString(PREF_LAST_ARTICLE_ID, null)
    }

    override fun saveNewsSources(sources: Set<String>) {
        prefs.edit()
            .putStringSet(PREF_SOURCES, sources)
            .apply()
    }

    override fun getNewsSources(): Set<String> {
        return prefs.getStringSet(PREF_SOURCES, null) ?: setOf()
    }

    companion object {
        private const val PREF_LAST_ARTICLE_ID = "pref_last_article_id"
        private const val PREF_SOURCES = "pref_sources"
    }
}