package com.pukachkosnt.newstask.models

import java.util.*

/**
 * UI model. Here is one additional parameter @collapsed - needed to control an item presentation
 */
data class ArticleUiModel (
    val title: String,
    val description: String,
    val urlToImage: String?,
    val publishedAt: Date,
    val sourceName: String,
    val url: String,
    val isFavorite: Boolean = false,
    val id: String = title + publishedAt.time.toString() + url
) {

    var collapsed: Boolean = true
        @Synchronized set

    // Toggle a @collapsed flag variable
    @Synchronized
    fun toggleCollapsed(): Boolean {
        collapsed = !collapsed
        return collapsed
    }
}
