package com.pukachkosnt.newstask

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.work.*
import com.pukachkosnt.newstask.di.module.*
import com.pukachkosnt.notifications.NotificationsNewArticleWorker
import com.pukachkosnt.notifications.PeriodicIntentGenerator
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    private val activityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        // prevents showing notification if an activity is started
        private val onShowNotification = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                resultCode = Activity.RESULT_CANCELED
            }
        }

        override fun onActivityStarted(activity: Activity) {
            val filter = IntentFilter(NotificationsNewArticleWorker.ACTION_SHOW_NOTIFICATION)
            activity.registerReceiver(
                onShowNotification,
                filter,
                NotificationsNewArticleWorker.PERMISSION_PRIVATE,
                null
            )
        }

        override fun onActivityStopped(activity: Activity) {
            activity.unregisterReceiver(onShowNotification)
        }

        override fun onActivityCreated(p0: Activity, p1: Bundle?) { }
        override fun onActivityResumed(p0: Activity) { }
        override fun onActivityPaused(p0: Activity) { }
        override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) { }
        override fun onActivityDestroyed(p0: Activity) { }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(appModule, viewModelModule, notificationsModule, dataModule, domainModule))
        }
        startNotifications()

        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    private fun startNotifications() {
        val prefs = applicationContext.getSharedPreferences(
            PREFS_PATH,
            Context.MODE_PRIVATE
        )

        if (prefs.getBoolean(getString(R.string.notifications_pref), true)
            && prefs.getInt(getString(R.string.time_interval_pref), 0) == 0) {

            prefs.edit()
                .putInt(
                    getString(R.string.time_interval_pref),
                    MIN_NOTIF_TIME_INTERVAL
                ).apply()
        }

        val periodicIntentGenerator: PeriodicIntentGenerator by inject()
        periodicIntentGenerator.start(MIN_NOTIF_TIME_INTERVAL_MILLS)
    }

    companion object {
        const val MIN_NOTIF_TIME_INTERVAL = 15 // minutes
        const val MIN_NOTIF_TIME_INTERVAL_MILLS: Long = MIN_NOTIF_TIME_INTERVAL * 1000 * 60L // mills
        const val PREFS_PATH = "com.pukachkosnt.newstask_preferences"
    }
}