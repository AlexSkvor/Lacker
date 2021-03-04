package com.lacker.staff.di

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

}