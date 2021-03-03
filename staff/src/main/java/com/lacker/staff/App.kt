package com.lacker.staff

import android.app.Application
import com.lacker.staff.di.AppModule
import com.lacker.staff.di.DaggerAppComponent
import com.lacker.staff.di.DependencyProvider
import com.lacker.utils.lifecycle.ActivityLifecycleLogger
import timber.log.Timber

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