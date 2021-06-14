package com.pukachkosnt.newstask

import androidx.lifecycle.*
import androidx.paging.*
import com.pukachkosnt.newstask.api.NewsApiFact
import com.pukachkosnt.newstask.api.NewsFetchRepository
import com.pukachkosnt.newstask.models.Article

private const val MAX_PAGES = 7
private const val BASE_URL = "https://newsapi.org/"
private const val PAGE_SIZE = 10

class NewsViewModel : ViewModel() {
    val newsItemsLiveData: LiveData<PagingData<Article>>
    private val mutableSearchQuery = MutableLiveData<String>()

    init {
        val newsApi = NewsApiFact.Builder()
            .addBaseUrl(BASE_URL)
            .build()
        newsItemsLiveData = Transformations.switchMap(mutableSearchQuery) {
            Pager(PagingConfig(PAGE_SIZE)) {
                NewsDataSource(NewsFetchRepository(newsApi), it ?: "", MAX_PAGES)   // set factory
            }.liveData.cachedIn(viewModelScope)
        }
        fetchNews()
    }

    fun fetchNews(query: String = "") {
        mutableSearchQuery.value = query
    }

    fun updateNews() {
        mutableSearchQuery.value = mutableSearchQuery.value
    }
}