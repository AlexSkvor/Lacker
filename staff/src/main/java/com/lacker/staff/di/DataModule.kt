package com.lacker.staff.di

import com.lacker.staff.data.storage.UserPrefs
import com.lacker.staff.data.storage.UserStorage
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DataModule {

    @Singleton
    @Binds
    fun bindUserStorage(userPrefs: UserPrefs): UserStorage

}