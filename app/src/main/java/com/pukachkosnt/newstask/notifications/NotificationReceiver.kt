package com.pukachkosnt.newstask.notifications

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class NotificationReceiver : BroadcastReceiver() {
    private val notificationUtil: NotificationUtil by inject(NotificationUtil::class.java)
    private val contentBuilder: NotificationContentBuilder by inject(
        NotificationContentBuilder::class.java
    )

    override fun onReceive(context: Context, intent: Intent?) {
        if (resultCode == Activity.RESULT_OK) {

            when (val requestCode = intent?.getIntExtra(REQUEST_CODE, 0)) {
                NOTIF_NEW_ARTICLES_ID -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        val descr = contentBuilder.makeContent()
                        descr?.let {
                            notificationUtil.notify(requestCode, it)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE = "REQUEST_CODE"

        const val NOTIF_NEW_ARTICLES_ID = 1
    }
}