package com.pukachkosnt.newstask.ui.listnews.all

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.pukachkosnt.domain.NewsDataSource
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.FavoritesRepository
import com.pukachkosnt.domain.repository.NewsRepository
import com.pukachkosnt.newstask.ui.listnews.BaseNewsViewModel
import com.pukachkosnt.newstask.ui.listnews.ListState

class ListNewsViewModel(
    private val newsRepository: NewsRepository,
    private val favoritesRepository: FavoritesRepository
) : BaseNewsViewModel(favoritesRepository) {
    private var loadedDataList: List<ArticleModel> = listOf() // stores the full list of loaded data

    init {
        fetchNews()
    }

    fun fetchNews() {
        pagerLiveData.removeObserver(pagerLiveDataObserver)

        pagerLiveData = Pager(PagingConfig(PAGE_SIZE)) {
            NewsDataSource(     // set factory
                newsRepository,
                favoritesRepository,
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
        _newsItemsLiveData.value = ListState.Filtered(
            loadedPagingData.filter {
                it.title.contains(trimQuery, true)
            },
            list.isEmpty()
        )
    }

    fun clearFilter() {
        // restores the full list of PagingData
        _newsItemsLiveData.value = ListState.Full(loadedPagingData)
    }

    override suspend fun addFavoriteArticleAsync(articleModel: ArticleModel): Result<ArticleModel> {
        val result = super.addFavoriteArticleAsync(articleModel)
        manageFavoriteArticlesAsync(articleModel, result, true)
        return result
    }

    override suspend fun deleteFavoriteArticleAsync(articleModel: ArticleModel): Result<ArticleModel> {
        val result = super.deleteFavoriteArticleAsync(articleModel)
        manageFavoriteArticlesAsync(articleModel, result, false)
        return result
    }

    private fun manageFavoriteArticlesAsync(
        articleModel: ArticleModel,
        result: Result<ArticleModel>,
        isFavorite: Boolean
    ) {
        fun condition(it: ArticleModel): ArticleModel =
            if (it.id == articleModel.id) { it.copy(isFavorite = isFavorite) }
            else { it }

        if (result.isSuccess) {
            loadedPagingData = loadedPagingData.map { condition(it) }

            val tempPagingData = _newsItemsLiveData.value?.data?.map { condition(it) }
                ?: PagingData.empty()

            _newsItemsLiveData.postValue(
                when (_newsItemsLiveData.value) {
                    is ListState.Filtered -> ListState.Filtered(tempPagingData, false)
                    else -> ListState.Full(tempPagingData)
                }
            )
        }
     }

    fun refreshFavoriteArticles(deletedItemsSet: HashSet<String>) {
        fun condition(it: ArticleModel): ArticleModel =
            if (deletedItemsSet.contains(it.id)) { it.copy(isFavorite = false) }
            else { it }

        loadedPagingData = loadedPagingData.map { condition(it) }
        val tempPagingData = _newsItemsLiveData.value?.data?.map { condition(it) } ?: PagingData.empty()
        _newsItemsLiveData.value = when (_newsItemsLiveData.value) {
            is ListState.Full -> ListState.Full(tempPagingData)
            else -> ListState.Filtered(tempPagingData, false)
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