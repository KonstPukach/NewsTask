package com.pukachkosnt.newstask.ui.listnews.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.BaseDBRepository
import com.pukachkosnt.newstask.ui.listnews.BaseNewsViewModel
import com.pukachkosnt.newstask.ui.listnews.ListState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val dbRepository: BaseDBRepository
) : BaseNewsViewModel(dbRepository) {
    private val _favoriteItemsLiveData: MutableLiveData<ListState> = MutableLiveData()
    override val newsItemsLiveData: LiveData<ListState>
        get() = _favoriteItemsLiveData

    // fragment result is a set of removed items
    private val _fragmentResult: HashSet<Long> = hashSetOf()
    val fragmentResult: Set<Long> = _fragmentResult

    init {
        fetchFavoriteArticles()
    }

    private fun fetchFavoriteArticles() {
        viewModelScope.launch {
            dbRepository.getAllArticlesFlow().cachedIn(viewModelScope).collect {
                _favoriteItemsLiveData.postValue(ListState.Full(it))
                loadedPagingData = it
                cancel()    // executes once
            }
        }
    }

    override fun deleteFavoriteArticle(articleModel: ArticleModel) {
        viewModelScope.launch {
            dbRepository.deleteArticle(articleModel)
            // manually removes an item from the source data
            loadedPagingData = loadedPagingData.filter {
                it.isFavorite != articleModel.isFavorite
            }
            _favoriteItemsLiveData.postValue(ListState.Full(loadedPagingData))
            _fragmentResult.add(articleModel.publishedAt.time)
        }
    }
}