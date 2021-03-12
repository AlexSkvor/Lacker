package com.lacker.staff.data.storage.restaurants

interface SignedBeforeRestaurantsStorage {

    fun addEmail(restaurantId: String, email: String)

    fun getEmail(restaurantId: String): String?

}