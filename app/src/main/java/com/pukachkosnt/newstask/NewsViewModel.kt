package com.pukachkosnt.newstask

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.pukachkosnt.newstask.api.NewsApiFact
import com.pukachkosnt.newstask.api.NewsFetchRepository
import com.pukachkosnt.newstask.models.Article
import java.util.*

private const val MAX_PAGES = 7
private const val BASE_URL = "https://newsapi.org/"

class NewsViewModel(querySearch: String = "") : ViewModel() {
    val newsItemsLiveData: LiveData<PagingData<Article>>

    init {
        val newsApi = NewsApiFact.Builder()
            .addBaseUrl(BASE_URL)
            .addSearchQuery(querySearch)
            .build()
        newsItemsLiveData = Pager(PagingConfig(10)) {
            NewsDataSource(NewsFetchRepository(newsApi), MAX_PAGES)   // set factory
        }.liveData.cachedIn(viewModelScope)
    }
}