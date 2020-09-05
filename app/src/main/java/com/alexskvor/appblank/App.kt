package com.alexskvor.appblank

import android.app.Application
import timber.log.Timber
import com.alexskvor.appblank.di.AppModule
import com.alexskvor.appblank.di.DaggerAppComponent
import com.alexskvor.appblank.di.DependencyProvider
import com.alexskvor.utils.lifecycle.ActivityLifecycleLogger

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initLogging()
        initAnalytics()
        initDi()
        registerActivityLifecycleCallbacks(ActivityLifecycleLogger)
    }

    private fun initLogging() {
        if (!BuildConfig.APP_CENTER_ENABLED) {
            Timber.plant(Timber.DebugTree())
        } else {
            TODO("AppCenter specific logging")
        }
    }

    private fun initDi(){
        DependencyProvider.get().component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    private fun initAnalytics() {
        //TODO
    }

}