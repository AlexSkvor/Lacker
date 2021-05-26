package com.lacker.staff.di

import com.lacker.staff.data.storage.menu.FileMenuManager
import com.lacker.staff.data.storage.menu.MenuManager
import com.lacker.staff.data.storage.user.UserPrefs
import com.lacker.staff.data.storage.user.UserStorage
import dagger.Binds
import dagger.Module
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