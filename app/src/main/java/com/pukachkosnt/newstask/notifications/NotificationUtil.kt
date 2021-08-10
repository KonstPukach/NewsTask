package com.pukachkosnt.newstask.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.pukachkosnt.notifications.R

// Manages notifications sending depending on API level

class NotificationUtil(
    private val appContext: Context,
    private val pendingIntent: PendingIntent
) {

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun notify(notifId: Int, contentText: String) {
        val title = appContext.getString(R.string.app_name)
        val notification = NotificationCompat.Builder(appContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setTicker(title)
            .setSmallIcon(R.drawable.ic_baseline_article_24)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        notificationManager.notify(
            notifId,
            notification
                .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
                .setContentText(contentText)
                .build()
        )
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"
        const val NOTIFICATION_CHANNEL = "notification_channel"
    }
}