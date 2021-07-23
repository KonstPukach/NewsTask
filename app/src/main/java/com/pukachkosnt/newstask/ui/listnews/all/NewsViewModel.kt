package com.pukachkosnt.newstask.ui.listnews.all

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.pukachkosnt.domain.NewsDataSource
import com.pukachkosnt.domain.repository.BaseApiRepository
import com.pukachkosnt.domain.repository.BaseDBRepository
import com.pukachkosnt.newstask.ui.listnews.BaseNewsViewModel
import com.pukachkosnt.newstask.ui.listnews.ListState

class NewsViewModel(
    private val apiRepository: BaseApiRepository,
    private val dbRepository: BaseDBRepository
) : BaseNewsViewModel(dbRepository) {

    init {
        fetchNews()
    }

    fun fetchNews() {
        pagerLiveData.removeObserver(pagerLiveDataObserver)

        pagerLiveData = Pager(PagingConfig(PAGE_SIZE)) {
            NewsDataSource(     // set factory
                apiRepository,
                dbRepository,
                MAX_PAGES
            ).also {
                loadedDataList = it.dataList
            }
        }.liveData.cachedIn(viewModelScope)

        pagerLiveData.observeForever(pagerLiveDataObserver)
    }

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

    fun refreshFavoriteArticles(deletedItemsSet: HashSet<Long>) {
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