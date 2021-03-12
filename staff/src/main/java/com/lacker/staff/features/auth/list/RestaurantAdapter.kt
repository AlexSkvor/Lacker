package com.lacker.staff.features.auth.list

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.staff.R
import com.lacker.staff.data.dto.restaurant.RestaurantDto
import kotlinx.android.synthetic.main.item_restaurant.*

fun getRestaurantAdapter(
    onClick: (RestaurantDto) -> Unit
) = adapterDelegateLayoutContainer<RestaurantsListItem.Restaurant, RestaurantsListItem>(
    R.layout.item_restaurant
) {

    restaurantViewItem.setOnClickListener { onClick(item.value) }

    bind {
        restaurantViewItem.restaurant = item.value
    }
}