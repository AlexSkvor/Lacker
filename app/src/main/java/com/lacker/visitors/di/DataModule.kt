package com.lacker.visitors.di

import dagger.Binds
import dagger.Module
import com.lacker.visitors.data.storage.UserPrefs
import com.lacker.visitors.data.storage.UserStorage
import com.lacker.visitors.data.storage.menu.FileMenuManager
import com.lacker.visitors.data.storage.menu.MenuManager
import javax.inject.Singleton

@Module
interface DataModule {

    @Singleton
    @Binds
    fun bindUserStorage(userPrefs: UserPrefs): UserStorage

    @Singleton
    @Binds
    fun bindMenuManager(manager: FileMenuManager): MenuManager
}