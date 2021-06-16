package com.pukachkosnt.newstask

class SearchViewState {
    var searchQuery: String = ""
    var state: State = State.CLOSED

    enum class State {
        FOCUSED_WITH_KEYBOARD,
        UNFOCUSED,
        CLOSED
    }
}