package com.lacker.visitors.di

import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import com.lacker.visitors.navigation.FastClickSafeRouter
import ru.terrakok.cicerone.Router
import javax.inject.Singleton

@Module
class NavigationModule {

    @Singleton
    @Provides
    fun provideCicerone(router: Router): Cicerone<Router> {
        return Cicerone.create(router)
    }

    @Singleton
    @Provides
    fun provideRouter(): Router {
        return FastClickSafeRouter()
    }

    @Singleton
    @Provides
    fun provideNavigatorHolder(cicerone: Cicerone<Router>): NavigatorHolder {
        return cicerone.navigatorHolder
    }

}