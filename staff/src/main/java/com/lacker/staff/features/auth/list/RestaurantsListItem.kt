package com.lacker.staff.features.auth.list

import com.lacker.staff.data.dto.restaurant.RestaurantDto

sealed class RestaurantsListItem {
    data class Restaurant(val value: RestaurantDto) : RestaurantsListItem()
    object Header : RestaurantsListItem()
}