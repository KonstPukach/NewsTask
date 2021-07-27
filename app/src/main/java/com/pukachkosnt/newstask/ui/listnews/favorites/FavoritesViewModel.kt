package com.pukachkosnt.newstask.ui.listnews.favorites

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.pukachkosnt.domain.FavoritesDataSource
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.BaseDBRepository
import com.pukachkosnt.newstask.ui.listnews.BaseNewsViewModel
import com.pukachkosnt.newstask.ui.listnews.ListState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async


class FavoritesViewModel(
    private val dbRepository: BaseDBRepository
) : BaseNewsViewModel() {
    // fragment result is a set of removed items
    private val _deletedItems: HashSet<String> = hashSetOf()
    val deletedItems: Set<String> = _deletedItems

    init {
        fetchFavoriteArticles()
    }

    private fun fetchFavoriteArticles() {
        pagerLiveData.removeObserver(pagerLiveDataObserver)

        pagerLiveData = Pager(PagingConfig(PAGE_SIZE)) {
            FavoritesDataSource(dbRepository, PAGE_SIZE)
        }.liveData.cachedIn(viewModelScope)

        pagerLiveData.observeForever(pagerLiveDataObserver)
    }

    override fun deleteFavoriteArticleAsync(
        articleModel: ArticleModel,
    ): Deferred<Result<ArticleModel>> {
        return processFavoriteArticleAsync(
            articleModel,
            false,
            _deletedItems::add
        )
    }

    override fun addFavoriteArticleAsync(
        articleModel: ArticleModel
    ): Deferred<Result<ArticleModel>> {
        return processFavoriteArticleAsync(
            articleModel,
            true,
            _deletedItems::remove
        )
    }

    private fun processFavoriteArticleAsync(
        articleModel: ArticleModel,
        isFavorite: Boolean,
        actionForSetOfRemovedItems: (String) -> Unit
    ): Deferred<Result<ArticleModel>> {
        return viewModelScope.async {
            val result =
                if (isFavorite) { dbRepository.addArticle(articleModel) }
                else { dbRepository.deleteArticle(articleModel) }

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
            result
        }
    }

    override fun onCleared() {
        super.onCleared()
        pagerLiveData.removeObserver(pagerLiveDataObserver)
    }

    companion object {
        private const val PAGE_SIZE = 30
    }
}