package com.pukachkosnt.newstask.di.module

import com.pukachkosnt.notifications.NotificationsNewArticleWorker
import com.pukachkosnt.notifications.PeriodicIntentGenerator
import com.pukachkosnt.notifications.WorkManagerPeriodicGenerator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val SEND_NOTIF_UNIQUE_NAME = "send_notification"

val notificationsModule = module {
    single<PeriodicIntentGenerator> {
        WorkManagerPeriodicGenerator(
            androidContext(),
            NotificationsNewArticleWorker::class.java,
            SEND_NOTIF_UNIQUE_NAME
        )
    }
}