package com.lacker.visitors.di

import dagger.Component
import com.lacker.visitors.MainActivity
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