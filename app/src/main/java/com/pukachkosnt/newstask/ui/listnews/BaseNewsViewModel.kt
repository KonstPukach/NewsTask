package com.pukachkosnt.newstask.ui.listnews

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.map
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.BaseDBRepository
import kotlinx.coroutines.launch


abstract class BaseNewsViewModel(
    private val dbRepository: BaseDBRepository
) : ViewModel() {
    protected val _newsItemsLiveData: MutableLiveData<ListState> = MutableLiveData()
    val newsItemsLiveData: LiveData<ListState>
        get() = _newsItemsLiveData
    protected var loadedPagingData: PagingData<ArticleModel> = PagingData.empty()

    protected var loadedDataList: List<ArticleModel> = listOf() // stores the full list of loaded data

    // paging liveData from Pager
    protected var pagerLiveData: LiveData<PagingData<ArticleModel>> = MutableLiveData()

    protected val pagerLiveDataObserver: Observer<PagingData<ArticleModel>> = Observer {
        _newsItemsLiveData.value = ListState.Full(it)
        loadedPagingData = it
    }

    open fun addFavoriteArticle(articleModel: ArticleModel) {
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