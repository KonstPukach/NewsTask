package com.pukachkosnt.newstask.ui.listnews.favorites

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.pukachkosnt.domain.FavoritesDataSource
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.FavoritesRepository
import com.pukachkosnt.newstask.ui.listnews.BaseNewsViewModel
import com.pukachkosnt.newstask.ui.listnews.ListState


class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : BaseNewsViewModel(favoritesRepository) {
    // fragment result is a set of removed items
    private val _deletedItems: HashSet<String> = hashSetOf()
    val deletedItems: Set<String> = _deletedItems

    init {
        fetchFavoriteArticles()
    }

    private fun fetchFavoriteArticles() {
        pagerLiveData.removeObserver(pagerLiveDataObserver)

        pagerLiveData = Pager(PagingConfig(PAGE_SIZE)) {
            FavoritesDataSource(favoritesRepository, PAGE_SIZE)
        }.liveData.cachedIn(viewModelScope)

        pagerLiveData.observeForever(pagerLiveDataObserver)
    }

    override suspend fun deleteFavoriteArticleAsync(
        articleModel: ArticleModel,
    ): Result<ArticleModel> {
        return manageFavoriteArticlesAsync(
            articleModel,
            false,
            _deletedItems::add
        )
    }

    override suspend fun addFavoriteArticleAsync(
        articleModel: ArticleModel
    ): Result<ArticleModel> {
        return manageFavoriteArticlesAsync(
            articleModel,
            true,
            _deletedItems::remove
        )
    }

    private suspend fun manageFavoriteArticlesAsync(
        articleModel: ArticleModel,
        isFavorite: Boolean,
        actionForSetOfRemovedItems: (String) -> Unit
    ): Result<ArticleModel> {
        val result =
            if (isFavorite) { favoritesRepository.addArticle(articleModel) }
            else { favoritesRepository.deleteArticle(articleModel) }

        if (result.isSuccess) {
            loadedPagingData = loadedPagingData.map {
                if (it.id == articleModel.id) {
                    it.copy(isFavorite = isFavorite)
                } else {
                    it
                }
            }
            _newsItemsLiveData.postValue(ListState.Full(loadedPagingData))
            actionForSetOfRemovedItems(articleModel.id)
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        pagerLiveData.removeObserver(pagerLiveDataObserver)
    }

    companion object {
        private const val PAGE_SIZE = 30
    }
}