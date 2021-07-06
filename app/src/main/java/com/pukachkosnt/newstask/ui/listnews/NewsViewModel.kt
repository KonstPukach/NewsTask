package com.pukachkosnt.newstask.ui.listnews

import androidx.lifecycle.*
import androidx.paging.*
import com.pukachkosnt.domain.NewsDataSource
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.BaseRepository

class NewsViewModel(
    private val newsRepository: BaseRepository
) : ViewModel() {
    private val _newsItemsLiveData: MutableLiveData<ListState> = MutableLiveData()
    val newsItemsLiveData: LiveData<ListState> = _newsItemsLiveData

    private var loadedDataList: List<ArticleModel> = listOf()   // stores the full list of loaded data

    // paging liveData from Pager
    private var pagerLiveData: LiveData<PagingData<ArticleModel>> = MutableLiveData()

    private var loadedPagingData: PagingData<ArticleModel> = PagingData.empty()

    private val pagerLiveDataObserver: Observer<PagingData<ArticleModel>> = Observer {
        _newsItemsLiveData.value = ListState.Full(it)
        loadedPagingData = it
    }

    init {
        fetchNews()
    }

    fun fetchNews() {
        pagerLiveData.removeObserver(pagerLiveDataObserver)

        pagerLiveData = Pager(PagingConfig(PAGE_SIZE)) {
            NewsDataSource(     // set factory
                newsRepository,
                "",
                MAX_PAGES
            ).also {
                loadedDataList = it.dataList
            }
        }.liveData.cachedIn(viewModelScope)

        pagerLiveData.observeForever(pagerLiveDataObserver)
    }

    // filtering received data and managing widgets states
    fun filterNews(query: String) {
        val trimQuery = query.trim()
        val list = loadedDataList.filter {
            it.title.contains(trimQuery, true)
        }
        val filteredPagingData = PagingData.from(list)
        _newsItemsLiveData.value = ListState.Filtered(filteredPagingData, list.isEmpty())
    }

    fun clearFilter() {
        _newsItemsLiveData.value =
            ListState.Full(loadedPagingData) // restore the full list of PagingData
    }

    override fun onCleared() {
        super.onCleared()
        pagerLiveData.removeObserver(pagerLiveDataObserver)
    }

    companion object {
        private const val MAX_PAGES = 7
        private const val PAGE_SIZE = 1
    }
}