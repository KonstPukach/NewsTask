package com.pukachkosnt.newstask.ui.listnews

import androidx.lifecycle.*
import androidx.paging.PagingData
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException


abstract class BaseNewsViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {
    protected val _newsItemsLiveData: MutableLiveData<ListState> = MutableLiveData()
    val newsItemsLiveData: LiveData<ListState>
        get() = _newsItemsLiveData
    protected var loadedPagingData: PagingData<ArticleModel> = PagingData.empty()

    // paging liveData from Pager
    protected var pagerLiveData: LiveData<PagingData<ArticleModel>> = MutableLiveData()

    private val _addFavoritesState: MutableStateFlow<Result<ArticleModel?>> =
        MutableStateFlow(Result.success(null))
    val addFavoritesState: StateFlow<Result<ArticleModel?>> = _addFavoritesState

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
            val result = if (article.isFavorite) { addFavoriteArticleAsync(article) }
            else { deleteFavoriteArticleAsync(article) }

            // need new instances to activate an observer
            _addFavoritesState.value = result.fold(
                { Result.success(article) },
                { Result.failure(IOException()) }
            )
        }
    }
}