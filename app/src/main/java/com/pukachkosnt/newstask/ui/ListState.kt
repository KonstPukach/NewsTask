package com.pukachkosnt.newstask.ui

import androidx.paging.PagingData
import com.pukachkosnt.domain.models.ArticleEntity

sealed class ListState {
    abstract val data: PagingData<ArticleEntity>

    data class Filtered(
        override val data: PagingData<ArticleEntity>,
        val isEmpty: Boolean
    ) : ListState()

    data class Full(override val data: PagingData<ArticleEntity>) : ListState()
}