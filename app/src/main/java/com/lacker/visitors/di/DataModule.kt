package com.lacker.visitors.di

import dagger.Binds
import dagger.Module
import com.lacker.visitors.data.storage.user.UserPrefs
import com.lacker.visitors.data.storage.user.UserStorage
import com.lacker.visitors.data.storage.menu.FileMenuManager
import com.lacker.visitors.data.storage.menu.MenuManager
import com.lacker.visitors.data.storage.session.SessionPrefs
import com.lacker.visitors.data.storage.session.SessionStorage
import javax.inject.Singleton

@Module
interface DataModule {

    @Singleton
    @Binds
    fun bindUserStorage(userPrefs: UserPrefs): UserStorage

    @Singleton
    @Binds
    fun bindSessionStorage(sessionPrefs: SessionPrefs): SessionStorage

    @Singleton
    @Binds
    fun bindMenuManager(manager: FileMenuManager): MenuManager
}