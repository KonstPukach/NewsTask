package com.pukachkosnt.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.pukachkosnt.domain.repository.LastViewedArticleRepository

class NewsPrefsRepository(
    context: Context,
    prefsPath: String
) : LastViewedArticleRepository {
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

    companion object {
        const val PREF_LAST_ARTICLE_ID = "pref_last_article_id"
    }
}