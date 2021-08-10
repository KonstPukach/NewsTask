package com.pukachkosnt.newstask.di.module

import android.app.PendingIntent
import android.content.Intent
import com.pukachkosnt.newstask.ui.listnews.NewsActivity
import com.pukachkosnt.newstask.ui.listnews.all.SearchViewState
import com.pukachkosnt.newstask.notifications.NotificationContentBuilder
import com.pukachkosnt.newstask.notifications.NotificationUtil
import com.pukachkosnt.newstask.notifications.newarticles.NewArticlesNotifBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    factory { SearchViewState() }

    single<NotificationContentBuilder> {
        NewArticlesNotifBuilder(
            get(),
            androidContext()
        )
    }

    single {
        val intent = Intent(androidContext(), NewsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        PendingIntent.getActivity(get(), 0, intent, 0)
    }

    single {
        NotificationUtil(androidContext(), get())
    }
}
