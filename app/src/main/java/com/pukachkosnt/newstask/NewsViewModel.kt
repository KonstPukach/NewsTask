package com.pukachkosnt.newstask

import androidx.lifecycle.*
import androidx.paging.*
import com.pukachkosnt.newstask.models.ArticleEntity
import com.pukachkosnt.newstask.repository.BaseRepository


class NewsViewModel(
    private val searchViewState: SearchViewState,
    private val recyclerViewState: NewsRecyclerViewState,
    private val newsRepository: BaseRepository
) : ViewModel() {
    companion object {
        private const val MAX_PAGES = 7
        private const val PAGE_SIZE = 1
    }
    private var fetched = false
    val newsItemsLiveData: LiveData<PagingData<ArticleEntity>>
    var recyclerViewItems: List<ArticleEntity> = listOf()   // stores the list of last filtered data

    private val mutableSearchQuery = MutableLiveData<String>()
    private val filteredItemsLiveData = MutableLiveData<PagingData<ArticleEntity>>()

    val getSearchViewState: SearchViewState
        get() = searchViewState

    val getRecyclerViewState: NewsRecyclerViewState
        get() = recyclerViewState

    init {
        newsItemsLiveData = Transformations.switchMap(mutableSearchQuery) {
            if (fetched) {
                filteredItemsLiveData.value = recyclerViewState.data
                filteredItemsLiveData
            } else {
                fetched = true
                Pager(PagingConfig(PAGE_SIZE)) {
                    NewsDataSource(
                        newsRepository,
                        mutableSearchQuery.value ?: "", MAX_PAGES
                    )   // set factory
                }.liveData.cachedIn(viewModelScope)
            }
        }
        fetchNews()
    }

    fun fetchNews(query: String = "") {
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
        searchViewState.apply {
            state = SearchViewState.State.UNFOCUSED
            searchQuery = query
        }
        recyclerViewState.apply {
            state = NewsRecyclerViewState.State.FILTERED
            val list = recyclerViewItems.filter {
                it.title.contains(query, true)
            }
            data = PagingData.from(list)
            isEmpty = list.isEmpty()
        }
        mutableSearchQuery.value = query
    }
}