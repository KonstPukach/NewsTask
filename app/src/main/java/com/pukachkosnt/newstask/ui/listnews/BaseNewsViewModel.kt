package com.pukachkosnt.newstask.ui.listnews

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.map
import com.pukachkosnt.domain.models.ArticleModel
import com.pukachkosnt.domain.repository.BaseDBRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async


abstract class BaseNewsViewModel : ViewModel() {
    protected val _newsItemsLiveData: MutableLiveData<ListState> = MutableLiveData()
    val newsItemsLiveData: LiveData<ListState>
        get() = _newsItemsLiveData
    protected var loadedPagingData: PagingData<ArticleModel> = PagingData.empty()

    // paging liveData from Pager
    protected var pagerLiveData: LiveData<PagingData<ArticleModel>> = MutableLiveData()

    protected val pagerLiveDataObserver: Observer<PagingData<ArticleModel>> = Observer {
        _newsItemsLiveData.value = ListState.Full(it)
        loadedPagingData = it
    }

    abstract fun addFavoriteArticleAsync(
        articleModel: ArticleModel
    ): Deferred<Result<ArticleModel>>

    abstract fun deleteFavoriteArticleAsync(
        articleModel: ArticleModel
    ): Deferred<Result<ArticleModel>>
}