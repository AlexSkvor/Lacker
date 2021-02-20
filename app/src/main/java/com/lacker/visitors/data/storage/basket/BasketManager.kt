package com.lacker.visitors.data.storage.basket

import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.dto.menu.OrderInfo

// TODO remove listeners and "sendBasketToServer"
interface BasketManager {

    companion object {
        const val MAX_BASKET_SIZE_FOR_ONE_MENU_ITEM = 99
    }

    suspend fun sendBasketToServer(): ApiCallResult<List<OrderInfo>>

    suspend fun addToBasket(
        restaurantId: String,
        portionId: String
    ): ApiCallResult<List<OrderInfo>>

    suspend fun removeFromBasket(
        restaurantId: String,
        portionId: String
    ): ApiCallResult<List<OrderInfo>>

    suspend fun getBasket(restaurantId: String): ApiCallResult<List<OrderInfo>>

    fun addBasketChangesListener(listenerOwner: Any, listener: (List<OrderInfo>) -> Unit)

    /**
     * Must be called before listener destroy to avoid leaks!
     */
    fun clearMyBasketChangesListeners(listenerOwner: Any)
}