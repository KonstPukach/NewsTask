package com.pukachkosnt.newstask.di.module

import com.pukachkosnt.newstask.NewsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        NewsViewModel(get(), get(), get())
    }
}