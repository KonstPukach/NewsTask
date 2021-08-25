package com.pukachkosnt.newstask.di.module

import com.pukachkosnt.newstask.ui.dialog.ChooseSourceViewModel
import com.pukachkosnt.newstask.ui.listnews.all.ListNewsViewModel
import com.pukachkosnt.newstask.ui.listnews.favorites.FavoritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        ListNewsViewModel(get(), get(), get(), get())
    }
    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        ChooseSourceViewModel(get(), get())
    }
}