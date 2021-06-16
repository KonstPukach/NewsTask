package com.pukachkosnt.newstask.di

import com.pukachkosnt.newstask.NewsViewModel
import com.pukachkosnt.newstask.repository.BaseRepository
import com.pukachkosnt.newstask.repository.NewsFetchRepository
import dagger.Component
import javax.inject.Singleton

@Component (modules = [ApiModule::class, ViewModelModule::class])
@Singleton
interface NewsComponent {
    fun getNewsRepository(): BaseRepository

    fun injectNewsViewModel(viewModel: NewsViewModel)
}