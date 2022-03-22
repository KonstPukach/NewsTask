package com.pukachkosnt.newstask.di.module

import com.pukachkosnt.domain.usecases.GetNewsSourcesUseCase
import com.pukachkosnt.domain.usecases.GetUnreadArticlesUseCase
import com.pukachkosnt.newstask.notifications.newarticles.NewArticlesNotifBuilder
import org.koin.dsl.module

val domainModule = module {
    single {
         GetUnreadArticlesUseCase(
             get(),
             get(),
             NewArticlesNotifBuilder.MAX_CHECKED_ARTICLES
         )
    }

    single {
        GetNewsSourcesUseCase(get(), get())
    }
}