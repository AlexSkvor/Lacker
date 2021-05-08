package com.lacker.staff.di

import com.lacker.staff.data.storage.restaurants.RestaurantPrefs
import com.lacker.staff.data.storage.restaurants.RestaurantStorage
import com.lacker.staff.data.storage.restaurants.SignedBeforeRestaurantsPrefs
import com.lacker.staff.data.storage.restaurants.SignedBeforeRestaurantsStorage
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
    fun bindRestaurantStorage(restaurantPrefs: RestaurantPrefs): RestaurantStorage

    @Singleton
    @Binds
    fun bindSignedBeforeRestaurantsStorage(signedBeforeRestaurantsPrefs: SignedBeforeRestaurantsPrefs): SignedBeforeRestaurantsStorage

}