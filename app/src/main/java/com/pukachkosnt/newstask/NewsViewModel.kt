package com.pukachkosnt.newstask

import androidx.lifecycle.*
import androidx.paging.*
import com.pukachkosnt.domain.models.ArticleEntity
import com.pukachkosnt.data.repository.BaseRepository
import com.pukachkosnt.domain.NewsDataSource


class NewsViewModel(
    val searchViewState: SearchViewState,
    val recyclerViewState: NewsRecyclerViewState,
    private val newsRepository: BaseRepository
) : ViewModel() {
    companion object {
        private const val MAX_PAGES = 7
        private const val PAGE_SIZE = 1
    }
    private var fetched = false

    private var _rotated = true     // set true as soon as device is rotated
    val rotated: Boolean
        get() = _rotated

    val newsItemsLiveData: LiveData<PagingData<ArticleEntity>>
    private var recyclerViewItems: List<ArticleEntity> = listOf()   // stores the list of last filtered data

    private val mutableSearchQuery = MutableLiveData<String>()
    private val filteredItemsLiveData = MutableLiveData<PagingData<ArticleEntity>>()

    init {
        newsItemsLiveData = Transformations.switchMap(mutableSearchQuery) {
            if (fetched) {
                filteredItemsLiveData.value = recyclerViewState.data
                filteredItemsLiveData
            } else {
                fetched = true
                val pager = Pager(PagingConfig(PAGE_SIZE)) {
                    NewsDataSource(     // set factory
                        newsRepository,
                        mutableSearchQuery.value ?: "",
                        MAX_PAGES,
                        this
                    ).addDataList {
                        recyclerViewItems = it
                    }
                }

                pager.liveData.cachedIn(viewModelScope)
            }
        }
        fetchNews()
        _rotated = true
    }

    fun fetchNews(query: String = "") {
        _rotated = false
        fetched = false
        searchViewState.state = SearchViewState.State.CLOSED
        recyclerViewState.apply {
            state = NewsRecyclerViewState.State.FULL
            data = PagingData.empty()   // reset the list of news
        }
        mutableSearchQuery.value = query
    }

    // filtering received data and managing widgets states
    fun filterNews(query: String) {
        _rotated = false
        val trimQuery = query.trim()
        searchViewState.apply {
            state = SearchViewState.State.UNFOCUSED
            searchQuery = query
        }
        recyclerViewState.apply {
            state = NewsRecyclerViewState.State.FILTERED
            val list = recyclerViewItems.filter {
                it.title.contains(trimQuery, true)
            }
            data = PagingData.from(list)
            isEmpty = list.isEmpty()
        }
        mutableSearchQuery.value = trimQuery
    }

    fun updateSearchViewState(
        hasFocus: Boolean? = null,
        searchQuery: String? = null
    ) {
        hasFocus?.let {
            searchViewState.state = if (it) {
                SearchViewState.State.FOCUSED_WITH_KEYBOARD
            } else {
                SearchViewState.State.UNFOCUSED
            }
        }
        searchQuery?.let {
            searchViewState.searchQuery = it
        }
    }

    fun rotate() {
        _rotated = true
    }
}