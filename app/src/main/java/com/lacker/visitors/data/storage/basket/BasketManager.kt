package com.lacker.visitors.data.storage.basket

import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.dto.menu.OrderInfo

interface BasketManager {

    companion object {
        const val MAX_BASKET_SIZE_FOR_ONE_MENU_ITEM = 99
    }

    suspend fun clearBasket(): ApiCallResult<List<OrderInfo>>

    suspend fun addToBasket(
        restaurantId: String,
        portionId: String
    ): ApiCallResult<List<OrderInfo>>

    suspend fun removeFromBasket(
        restaurantId: String,
        portionId: String
    ): ApiCallResult<List<OrderInfo>>

    suspend fun getBasket(restaurantId: String): ApiCallResult<List<OrderInfo>>

}