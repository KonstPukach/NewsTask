package com.pukachkosnt.newstask.di.module

import com.pukachkosnt.newstask.ui.NewsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        NewsViewModel(get())
    }
}