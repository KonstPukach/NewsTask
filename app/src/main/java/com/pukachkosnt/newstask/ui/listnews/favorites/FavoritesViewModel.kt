package com.pukachkosnt.newstask.ui.listnews.favorites

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.pukachkosnt.domain.FavoritesDataSource
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.BaseDBRepository
import com.pukachkosnt.newstask.ui.listnews.BaseNewsViewModel
import kotlinx.coroutines.launch


class FavoritesViewModel(
    private val dbRepository: BaseDBRepository
) : BaseNewsViewModel(dbRepository) {
    // fragment result is a set of removed items
    private val _deletedItems: HashSet<Long> = hashSetOf()
    val deletedItems: Set<Long> = _deletedItems

    init {
        fetchFavoriteArticles()
    }

    private fun fetchFavoriteArticles() {
        pagerLiveData.removeObserver(pagerLiveDataObserver)

        pagerLiveData = Pager(PagingConfig(PAGE_SIZE)) {
            FavoritesDataSource(dbRepository, PAGE_SIZE).also {
                loadedDataList = it.dataList
            }
        }.liveData.cachedIn(viewModelScope)

        pagerLiveData.observeForever(pagerLiveDataObserver)
    }

    override fun deleteFavoriteArticle(articleModel: ArticleModel) {
        viewModelScope.launch {
            dbRepository.deleteArticle(articleModel)
            _deletedItems.add(articleModel.publishedAt.time)
        }
    }

    override fun addFavoriteArticle(articleModel: ArticleModel) {
        viewModelScope.launch {
            dbRepository.addArticle(articleModel)
            _deletedItems.remove(articleModel.publishedAt.time)
        }
    }

    override fun onCleared() {
        super.onCleared()
        pagerLiveData.removeObserver(pagerLiveDataObserver)
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}