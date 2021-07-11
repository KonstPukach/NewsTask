package com.pukachkosnt.newstask.ui.listnews

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.BaseDBRepository
import kotlinx.coroutines.launch

abstract class BaseNewsViewModel(
    private val dbRepository: BaseDBRepository
) : ViewModel() {
    abstract val newsItemsLiveData: LiveData<ListState>
    protected var loadedPagingData: PagingData<ArticleModel> = PagingData.empty()

    fun addFavoriteArticle(articleModel: ArticleModel) {
        viewModelScope.launch {
            dbRepository.addArticle(articleModel)
        }
    }

    open fun deleteFavoriteArticle(articleModel: ArticleModel) {
        viewModelScope.launch {
            dbRepository.deleteArticle(articleModel)
        }
    }
}