package com.pukachkosnt.newstask.ui.listnews.all

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.pukachkosnt.domain.NewsDataSource
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.BaseApiRepository
import com.pukachkosnt.domain.repository.BaseDBRepository
import com.pukachkosnt.newstask.ui.listnews.BaseNewsViewModel
import com.pukachkosnt.newstask.ui.listnews.ListState

class NewsViewModel(
    private val apiRepository: BaseApiRepository,
    private val dbRepository: BaseDBRepository
) : BaseNewsViewModel(dbRepository) {
    private val _newsItemsLiveData: MutableLiveData<ListState> = MutableLiveData()
    override val newsItemsLiveData: LiveData<ListState> = _newsItemsLiveData

    private var loadedDataList: List<ArticleModel> = listOf()   // stores the full list of loaded data

    // paging liveData from Pager
    private var pagerLiveData: LiveData<PagingData<ArticleModel>> = MutableLiveData()

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
                apiRepository,
                dbRepository,
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
        // restores the full list of PagingData
        _newsItemsLiveData.value = ListState.Full(loadedPagingData)
    }

    suspend fun refreshFavoriteArticles(deletedItemsSet: HashSet<Long>) {
        loadedDataList.forEach {
            if (deletedItemsSet.contains(it.publishedAt.time)) {
                it.isFavorite = false
            }
        }
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