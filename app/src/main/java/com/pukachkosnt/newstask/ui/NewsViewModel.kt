package com.pukachkosnt.newstask.ui

import androidx.lifecycle.*
import androidx.paging.*
import com.pukachkosnt.domain.NewsDataSource
import com.pukachkosnt.domain.models.ArticleEntity
import com.pukachkosnt.domain.repository.BaseRepository

class NewsViewModel(
    private val newsRepository: BaseRepository
) : ViewModel() {
    lateinit var listState: ListState
        private set

    private val _newsItemsLiveData: MediatorLiveData<PagingData<ArticleEntity>> = MediatorLiveData()
    val newsItemsLiveData: LiveData<PagingData<ArticleEntity>> = _newsItemsLiveData

    private var loadedDataList: List<ArticleEntity> = listOf()   // stores the full list of loaded data

    // paging liveData from Pager
    private var pagerLiveData: LiveData<PagingData<ArticleEntity>> = MutableLiveData()

    private var loadedPagingData: PagingData<ArticleEntity> = PagingData.empty()

    private val pagerLiveDataObserver: Observer<PagingData<ArticleEntity>> = Observer {
        _newsItemsLiveData.value = it
        loadedPagingData = it
    }

    init {
        fetchNews()
    }

    fun fetchNews() {
        listState = ListState.Full(loadedPagingData)

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
        listState = ListState.Filtered(filteredPagingData, list.isEmpty())
        _newsItemsLiveData.value = filteredPagingData
    }

    fun clearFilter() {
        listState = ListState.Full(loadedPagingData)
        _newsItemsLiveData.value = loadedPagingData     // restore the full list of PagingData
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