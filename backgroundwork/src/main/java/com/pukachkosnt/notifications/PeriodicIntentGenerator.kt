package com.pukachkosnt.notifications

interface PeriodicIntentGenerator {
    fun start(timeInterval: Long)
    fun changeInterval(timeInterval: Long)
    fun cancel()
}