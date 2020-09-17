package com.lacker.visitors

import android.app.Application
import timber.log.Timber
import com.lacker.visitors.di.AppModule
import com.lacker.visitors.di.DaggerAppComponent
import com.lacker.visitors.di.DependencyProvider
import com.lacker.utils.lifecycle.ActivityLifecycleLogger

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initLogging()
        initAnalytics()
        initDi()
        registerActivityLifecycleCallbacks(ActivityLifecycleLogger)
    }

    private fun initLogging() {
        Timber.plant(Timber.DebugTree()) // TODO condition for only debug mode
    }

    private fun initDi() {
        DependencyProvider.get().component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    private fun initAnalytics() {
        //TODO
    }

}