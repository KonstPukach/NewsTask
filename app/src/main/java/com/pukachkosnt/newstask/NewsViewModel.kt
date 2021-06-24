package com.pukachkosnt.newstask

import androidx.lifecycle.*
import androidx.paging.*
import com.pukachkosnt.domain.NewsDataSource
import com.pukachkosnt.domain.models.ArticleEntity
import com.pukachkosnt.domain.repository.BaseRepository


class NewsViewModel(
    private var _searchViewState: SearchViewState,
    private var _recyclerViewState: NewsRecyclerViewState,
    private val newsRepository: BaseRepository
) : ViewModel() {
    companion object {
        private const val MAX_PAGES = 7
        private const val PAGE_SIZE = 1
    }

    val searchViewState: SearchViewState
        get() = _searchViewState
    val recyclerViewState: NewsRecyclerViewState
        get() = _recyclerViewState

    private val _newsItemsLiveData: MediatorLiveData<PagingData<ArticleEntity>> = MediatorLiveData()
    val newsItemsLiveData: LiveData<PagingData<ArticleEntity>> = _newsItemsLiveData

    private var loadedDataList: List<ArticleEntity> = listOf()   // stores the full list of loaded data

    private val filteredItemsLiveData = MutableLiveData<PagingData<ArticleEntity>>()

    private var pagerLiveData: LiveData<PagingData<ArticleEntity>> = MutableLiveData()

    private var loadedPagingData: PagingData<ArticleEntity> = PagingData.empty()

    init {
        fetchNews()
    }

    fun fetchNews() {
        _searchViewState = _searchViewState.copy(state = SearchViewState.State.CLOSED)
        _recyclerViewState = _recyclerViewState.copy(state = NewsRecyclerViewState.State.FULL)

        // remove previous source
        _newsItemsLiveData.removeSource(pagerLiveData)

        pagerLiveData = Pager(PagingConfig(PAGE_SIZE)) {
            NewsDataSource(     // set factory
                newsRepository,
                "",
                MAX_PAGES
            ).also {
                loadedDataList = it.dataList
            }
        }.liveData.cachedIn(viewModelScope)

        // add new source
        _newsItemsLiveData.addSource(pagerLiveData) {
            _newsItemsLiveData.value = it
            loadedPagingData = it
        }
    }

    // filtering received data and managing widgets states
    fun filterNews(query: String) {
        val trimQuery = query.trim()
        _searchViewState = _searchViewState.copy(
            state = SearchViewState.State.UNFOCUSED,
            searchQuery = query
        )
        val list = loadedDataList.filter {
            it.title.contains(trimQuery, true)
        }
        _recyclerViewState = _recyclerViewState.copy(
            state = NewsRecyclerViewState.State.FILTERED,
            isEmpty = list.isEmpty()
        )
        _newsItemsLiveData.removeSource(filteredItemsLiveData)  // remove previous source
        filteredItemsLiveData.value = PagingData.from(list)
        _newsItemsLiveData.addSource(filteredItemsLiveData) {
            _newsItemsLiveData.value = it
        }
    }

    fun restorePagingData() {
        _searchViewState = _searchViewState.copy(state = SearchViewState.State.CLOSED)
        _recyclerViewState = _recyclerViewState.copy(state = NewsRecyclerViewState.State.FULL)

        _newsItemsLiveData.value = loadedPagingData     // restore the full list of PagingData
    }

    fun updateSearchViewState(
        hasFocus: Boolean? = null,
        searchQuery: String? = null
    ) {
        var state = _searchViewState.state
        var query = _searchViewState.searchQuery

        hasFocus?.let {
            state = if (it) {
                SearchViewState.State.FOCUSED_WITH_KEYBOARD
            } else {
                SearchViewState.State.UNFOCUSED
            }
        }
        searchQuery?.let { query = it }

        _searchViewState = _searchViewState.copy(state = state, searchQuery = query)
    }
}