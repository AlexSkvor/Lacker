package com.lacker.visitors.di

import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import com.lacker.visitors.navigation.FastClickSafeRouter
import javax.inject.Singleton

@Module
class NavigationModule {

    @Singleton
    @Provides
    fun provideCicerone(router: FastClickSafeRouter): Cicerone<FastClickSafeRouter> {
        return Cicerone.create(router)
    }

    @Singleton
    @Provides
    fun provideRouter(): FastClickSafeRouter {
        return FastClickSafeRouter()
    }

    @Singleton
    @Provides
    fun provideNavigatorHolder(cicerone: Cicerone<FastClickSafeRouter>): NavigatorHolder {
        return cicerone.navigatorHolder
    }

}