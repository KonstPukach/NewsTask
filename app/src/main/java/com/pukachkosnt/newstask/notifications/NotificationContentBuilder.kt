package com.pukachkosnt.newstask.notifications

interface NotificationContentBuilder {
    suspend fun makeContent(): String?
}