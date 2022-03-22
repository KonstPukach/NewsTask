package com.pukachkosnt.newstask.ui.listnews.all

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.pukachkosnt.domain.NewsDataSource
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.FavoritesRepository
import com.pukachkosnt.domain.repository.LastViewedArticleRepository
import com.pukachkosnt.domain.repository.NewsByTimeIntervalRepository
import com.pukachkosnt.newstask.models.ArticleUiModel
import com.pukachkosnt.newstask.models.mappers.mapToDomainModel
import com.pukachkosnt.domain.repository.SourcesIdsRepository
import com.pukachkosnt.newstask.ui.listnews.BaseNewsViewModel
import com.pukachkosnt.newstask.ui.listnews.ListState

class ListNewsViewModel(
    private val newsByTimeIntervalRepository: NewsByTimeIntervalRepository,
    private val favoritesRepository: FavoritesRepository,
    private val lastViewedArticleRepository: LastViewedArticleRepository,
    private val sourcesIdsRepository: SourcesIdsRepository
) : BaseNewsViewModel(favoritesRepository) {
    private var loadedDataList: List<ArticleModel> = listOf() // stores the full list of loaded data
    private var sources: Set<String> = setOf()
    init {
        refreshSources()
        fetchNews()
    }

    fun fetchNews() {
        pagerLiveData.removeObserver(pagerLiveDataObserver)
        pagerLiveData = Pager(PagingConfig(PAGE_SIZE)) {
            NewsDataSource(     // set factory
                newsByTimeIntervalRepository,
                favoritesRepository,
                lastViewedArticleRepository,
                MAX_PAGES,
                sources
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

    override suspend fun addFavoriteArticleAsync(articleModel: ArticleUiModel): Result<ArticleModel> {
        val result = super.addFavoriteArticleAsync(articleModel)
        manageFavoriteArticlesAsync(articleModel.mapToDomainModel(), result, true)
        return result
    }

    override suspend fun deleteFavoriteArticleAsync(articleModel: ArticleUiModel): Result<ArticleModel> {
        val result = super.deleteFavoriteArticleAsync(articleModel)
        manageFavoriteArticlesAsync(articleModel.mapToDomainModel(), result, false)
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

    fun refreshSources() {
        sources = sourcesIdsRepository.getNewsSources()
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