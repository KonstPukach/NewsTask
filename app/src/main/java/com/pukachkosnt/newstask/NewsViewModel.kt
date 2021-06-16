package com.pukachkosnt.newstask

import androidx.lifecycle.*
import androidx.paging.*
import com.pukachkosnt.newstask.di.DaggerNewsComponent
import com.pukachkosnt.newstask.di.NewsComponent
import com.pukachkosnt.newstask.models.Article
import com.pukachkosnt.newstask.models.ArticleEntity
import com.pukachkosnt.newstask.repository.NewsFetchRepository
import javax.inject.Inject


class NewsViewModel : ViewModel() {
    companion object {
        private const val MAX_PAGES = 7
        private const val PAGE_SIZE = 1
    }
    val newsItemsLiveData: LiveData<PagingData<ArticleEntity>>
    var recyclerViewItems: List<ArticleEntity> = listOf()   // stores the list of last filtered data

    private val mutableSearchQuery = MutableLiveData<String>()

    @Inject lateinit var searchViewState: SearchViewState
    @Inject lateinit var recyclerViewState: NewsRecyclerViewState

    init {
        val newsComponent: NewsComponent = DaggerNewsComponent.create()
        newsComponent.injectNewsViewModel(this)

        newsItemsLiveData = Transformations.switchMap(mutableSearchQuery) {
            Pager(PagingConfig(PAGE_SIZE)) {
                NewsDataSource(
                    newsComponent.getNewsRepository(),
                    mutableSearchQuery.value ?: "", MAX_PAGES)   // set factory
            }.liveData.cachedIn(viewModelScope)
        }
        fetchNews()
    }

    fun fetchNews(query: String = "") {
        mutableSearchQuery.value = query
    }
}