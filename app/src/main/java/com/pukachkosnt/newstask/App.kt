package com.pukachkosnt.newstask

import android.app.Application
import com.pukachkosnt.newstask.di.module.appModule
import com.pukachkosnt.newstask.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(appModule, viewModelModule))
        }
    }
}