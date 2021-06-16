package com.pukachkosnt.newstask

import androidx.paging.PagingData
import com.pukachkosnt.newstask.models.ArticleEntity

class NewsRecyclerViewState {
    var state: State = State.FULL
    var data: PagingData<ArticleEntity> = PagingData.empty()

    enum class State {
        FULL,   // When data is shown without filters
        FILTERED    // When data is shown with filters. Then we should stop paging.
    }
}