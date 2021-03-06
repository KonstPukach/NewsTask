package com.pukachkosnt.newstask.ui.listnews.all

data class SearchViewState (
    val searchQuery: String = "",
    val state: State = State.CLOSED
) {
    enum class State {
        FOCUSED_WITH_KEYBOARD,
        UNFOCUSED,
        CLOSED
    }
}