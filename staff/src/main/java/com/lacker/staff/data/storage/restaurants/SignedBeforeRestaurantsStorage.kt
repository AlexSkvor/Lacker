package com.lacker.staff.data.storage.restaurants

interface SignedBeforeRestaurantsStorage {

    var restaurantCodes: Set<String>

    fun addEmail(restaurantId: String, email: String)

    fun getEmail(restaurantId: String): String?

}