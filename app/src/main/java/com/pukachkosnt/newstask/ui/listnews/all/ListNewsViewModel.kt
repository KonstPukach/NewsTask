package com.pukachkosnt.newstask.ui.listnews.all

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.pukachkosnt.domain.NewsDataSource
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.NewsRepository
import com.pukachkosnt.domain.repository.FavoritesRepository
import com.pukachkosnt.newstask.ui.listnews.BaseNewsViewModel
import com.pukachkosnt.newstask.ui.listnews.ListState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class ListNewsViewModel(
    private val newsRepository: NewsRepository,
    private val favoritesRepository: FavoritesRepository
) : BaseNewsViewModel() {
    private var loadedDataList: List<ArticleModel> = listOf() // stores the full list of loaded data
    private var currentlyShownPagingNews: PagingData<ArticleModel> = PagingData.empty()

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
        currentlyShownPagingNews = loadedPagingData
    }

    fun filterNews(query: String) {
        val trimQuery = query.trim()
        currentlyShownPagingNews = loadedPagingData.filter {
            it.title.contains(trimQuery, true)
        }
        val list = loadedDataList.filter {
            it.title.contains(trimQuery, true)
        }
        _newsItemsLiveData.value = ListState.Filtered(currentlyShownPagingNews, list.isEmpty())
    }

    fun clearFilter() {
        // restores the full list of PagingData
        _newsItemsLiveData.value = ListState.Full(loadedPagingData)
    }

    override fun addFavoriteArticleAsync(
        articleModel: ArticleModel
    ): Deferred<Result<ArticleModel>> {
        return manageFavoriteArticlesAsync(articleModel, true)
    }

    override fun deleteFavoriteArticleAsync(
        articleModel: ArticleModel
    ): Deferred<Result<ArticleModel>> {
        return manageFavoriteArticlesAsync(articleModel, false)
    }

    private fun manageFavoriteArticlesAsync(
        articleModel: ArticleModel,
        isFavorite: Boolean
    ): Deferred<Result<ArticleModel>> {
        fun condition(it: ArticleModel): ArticleModel =
            if (it.id == articleModel.id) { it.copy(isFavorite = isFavorite) }
            else { it }

        return viewModelScope.async {
            val result =
                if (isFavorite) { favoritesRepository.addArticle(articleModel) }
                else { favoritesRepository.deleteArticle(articleModel) }

            if (result.isSuccess) {
                loadedPagingData = loadedPagingData.map { condition(it) }
                currentlyShownPagingNews = currentlyShownPagingNews.map { condition(it) }
                _newsItemsLiveData.postValue(ListState.Full(currentlyShownPagingNews))
            }
            result
        }
    }

    fun refreshFavoriteArticles(deletedItemsSet: HashSet<String>) {
        fun condition(it: ArticleModel): ArticleModel =
            if (deletedItemsSet.contains(it.id)) { it.copy(isFavorite = false) }
            else { it }

        loadedPagingData = loadedPagingData.map { condition(it) }
        currentlyShownPagingNews = currentlyShownPagingNews.map { condition(it) }
        _newsItemsLiveData.value = when (_newsItemsLiveData.value) {
            is ListState.Full -> ListState.Full(currentlyShownPagingNews)
            else -> ListState.Filtered(currentlyShownPagingNews, false)
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