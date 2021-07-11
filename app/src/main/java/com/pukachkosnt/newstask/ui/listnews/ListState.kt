package com.pukachkosnt.newstask.ui.listnews

import androidx.paging.PagingData
import com.pukachkosnt.domain.models.ArticleModel

sealed class ListState {
    abstract val data: PagingData<ArticleModel>

    data class Filtered(
        override val data: PagingData<ArticleModel>,
        val isEmpty: Boolean
    ) : ListState()

    data class Full(
        override val data: PagingData<ArticleModel>
    ) : ListState()
}