package com.lacker.staff.features.auth.list

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.lacker.staff.data.dto.restaurant.RestaurantDto

fun getRestaurantSelectingAdapter(
    onRestaurantSelected: (RestaurantDto) -> Unit
) = AsyncListDifferDelegationAdapter(
    restaurantsDiffUtil,
    getHeaderAdapter(),
    getRestaurantAdapter(onRestaurantSelected)
)

val restaurantsDiffUtil by lazy {
    object : DiffUtil.ItemCallback<RestaurantsListItem>() {
        override fun areItemsTheSame(
            oldItem: RestaurantsListItem,
            newItem: RestaurantsListItem
        ): Boolean {
            return when {
                oldItem is RestaurantsListItem.Restaurant
                        && newItem is RestaurantsListItem.Restaurant
                -> oldItem.value.id == newItem.value.id
                else -> oldItem == newItem
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: RestaurantsListItem,
            newItem: RestaurantsListItem
        ): Boolean = oldItem == newItem
    }
}