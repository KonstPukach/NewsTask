package com.pukachkosnt.notifications

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

// Manages work requests

class WorkManagerPeriodicGenerator(
    private val context: Context,
    private val workerClass: Class<out ListenableWorker>,
    private val workUniqueName: String
) : PeriodicIntentGenerator {
    private fun startWithPolicy(timeInterval: Long, workPolicy: ExistingPeriodicWorkPolicy) {
        val workRequest = PeriodicWorkRequest.Builder(
            workerClass,
            timeInterval,
            TimeUnit.MILLISECONDS
        ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                workUniqueName,
                workPolicy,
                workRequest
            )
    }

    override fun start(timeInterval: Long) =
        startWithPolicy(timeInterval, ExistingPeriodicWorkPolicy.KEEP)

    override fun changeInterval(timeInterval: Long) =
        startWithPolicy(timeInterval, ExistingPeriodicWorkPolicy.REPLACE)

    override fun cancel() {
        WorkManager
            .getInstance(context)
            .cancelUniqueWork(workUniqueName)
    }

    companion object {
        private const val TAG = "NotifWorkManagerUtil"
    }
}