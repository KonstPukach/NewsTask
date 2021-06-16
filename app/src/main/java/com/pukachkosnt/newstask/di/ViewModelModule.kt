package com.pukachkosnt.newstask.di

import com.pukachkosnt.newstask.NewsRecyclerViewState
import com.pukachkosnt.newstask.SearchViewState
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {
    @Provides
    fun provideSearchViewState() = SearchViewState()

    @Provides
    fun provideNewsRecyclerViewState() = NewsRecyclerViewState()
}