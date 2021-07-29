package com.pukachkosnt.newstask.ui.listnews

import androidx.lifecycle.*
import androidx.paging.PagingData
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


abstract class BaseNewsViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    protected val _newsItemsLiveData: MutableLiveData<ListState> = MutableLiveData()
    val newsItemsLiveData: LiveData<ListState>
        get() = _newsItemsLiveData
    protected var loadedPagingData: PagingData<ArticleModel> = PagingData.empty()

    // paging liveData from Pager
    protected var pagerLiveData: LiveData<PagingData<ArticleModel>> = MutableLiveData()

    // emits Unit to do some action on add to favorites exception
    private val _addFavoritesOnError: MutableSharedFlow<Unit> = MutableSharedFlow(0)
    val addFavoritesOnError: SharedFlow<Unit> = _addFavoritesOnError

    protected val pagerLiveDataObserver: Observer<PagingData<ArticleModel>> = Observer {
        _newsItemsLiveData.value = ListState.Full(it)
        loadedPagingData = it
    }

    open suspend fun addFavoriteArticleAsync(
        articleModel: ArticleModel
    ): Result<ArticleModel> {
        return favoritesRepository.addArticle(articleModel)
    }

    open suspend fun deleteFavoriteArticleAsync(
        articleModel: ArticleModel
    ): Result<ArticleModel> {
        return favoritesRepository.deleteArticle(articleModel)
    }

    fun onFavoriteClicked(article: ArticleModel) {
        viewModelScope.launch {
            val newArticle = article.copy(isFavorite = !article.isFavorite)

            val result =
                if (newArticle.isFavorite) { addFavoriteArticleAsync(newArticle) }
                else { deleteFavoriteArticleAsync(newArticle) }

            if (result.isFailure) {
                _addFavoritesOnError.emit(Unit)
            }
        }
    }
}