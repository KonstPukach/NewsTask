package com.pukachkosnt.newstask.di.module

import com.pukachkosnt.newstask.ui.listnews.all.NewsViewModel
import com.pukachkosnt.newstask.ui.listnews.favorites.FavoritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        NewsViewModel(get(), get())
    }
    viewModel {
        FavoritesViewModel(get())
    }
}