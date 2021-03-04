package com.lacker.staff.data.storage.restaurants

interface SignedBeforeRestaurantsStorage {

    var restaurantIds: Set<String>

    var restaurantIdToEmailMap: Map<String, String>

}