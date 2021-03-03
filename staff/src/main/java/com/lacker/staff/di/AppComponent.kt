package com.lacker.staff.di

import com.lacker.staff.MainActivity
import dagger.Component
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