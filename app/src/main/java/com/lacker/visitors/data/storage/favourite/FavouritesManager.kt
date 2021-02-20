package com.lacker.visitors.data.storage.favourite

import com.lacker.visitors.data.api.ApiCallResult

interface FavouritesManager {

    suspend fun getFavourites(restaurantId: String): ApiCallResult<List<String>>

    suspend fun addToFavourites(
        restaurantId: String,
        menuItemId: String
    ): ApiCallResult<List<String>>

    suspend fun removeFromFavourites(
        restaurantId: String,
        menuItemId: String
    ): ApiCallResult<List<String>>

}