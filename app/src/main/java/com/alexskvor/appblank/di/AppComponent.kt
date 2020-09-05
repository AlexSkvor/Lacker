package com.alexskvor.appblank.di

import dagger.Component
import com.alexskvor.appblank.MainActivity
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NavigationModule::class,
        ViewModelModule::class,
        UtilsModule::class,
        DataModule::class,
        ApiModule::class
    ]
)
interface AppComponent {
    fun inject(appActivity: MainActivity)
}