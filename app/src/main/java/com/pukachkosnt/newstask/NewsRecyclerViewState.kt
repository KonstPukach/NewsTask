package com.pukachkosnt.newstask

data class NewsRecyclerViewState (
    val state: State = State.FULL,
    val isEmpty: Boolean = true
) {
    enum class State {
        FULL,   // When data is shown without filters
        FILTERED    // When data is shown with filters. Then we should stop paging.
    }
}