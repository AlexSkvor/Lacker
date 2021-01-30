package com.lacker.visitors.data.storage.basket

import com.lacker.visitors.data.api.ApiCallResult

interface BasketManager { // TODO rework with orderOnfo!

    suspend fun sendBasketToServer(): ApiCallResult<Unit>

    suspend fun addToBasket(restaurantId: String, portion: BasketIdPair): ApiCallResult<Unit>

    suspend fun removeFromBasket(restaurantId: String, portion: BasketIdPair): ApiCallResult<Unit>

    suspend fun getBasket(restaurantId: String): ApiCallResult<List<BasketIdPair>>

    fun addBasketChangesListener(listenerOwner: Any, listener: (List<BasketIdPair>) -> Unit)

    /**
     * Must be called before listener destroy to avoid leaks!
     */
    fun clearMyBasketChangesListeners(listenerOwner: Any)
}