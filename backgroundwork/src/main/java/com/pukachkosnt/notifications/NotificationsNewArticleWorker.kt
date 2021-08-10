package com.pukachkosnt.notifications

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/*
*  Sends a broadcast with a notification Id
 */

class NotificationsNewArticleWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val intent = Intent(ACTION_SHOW_NOTIFICATION).apply {
            putExtra(
                REQUEST_CODE,
                NOTIF_NEW_ARTICLES_ID
            )
        }

        applicationContext.sendOrderedBroadcast(
            intent,
            PERMISSION_PRIVATE
        )

        return Result.success()
    }

    companion object {
        const val ACTION_SHOW_NOTIFICATION = "com.pukachkosnt.newstask.SHOW_NOTIFICATION"
        const val PERMISSION_PRIVATE = "com.pukachkosnt.newstask.PRIVATE"

        private const val REQUEST_CODE = "REQUEST_CODE"
        private const val NOTIF_NEW_ARTICLES_ID = 1
    }
}