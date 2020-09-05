package com.alexskvor.appblank.di

import dagger.Binds
import dagger.Module
import com.alexskvor.appblank.data.storage.UserPrefs
import com.alexskvor.appblank.data.storage.UserStorage
import javax.inject.Singleton

@Module
interface DataModule {

    @Singleton
    @Binds
    fun bindUserStorage(userPrefs: UserPrefs): UserStorage

}