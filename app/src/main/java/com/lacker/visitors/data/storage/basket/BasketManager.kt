package com.lacker.visitors.data.storage.basket

interface BasketManager {

    suspend fun sendBasketToServer()

    suspend fun addToBasket(restaurantId: String, portion: BasketIdPair)

    suspend fun removeFromBasket(restaurantId: String, portion: BasketIdPair)

    suspend fun getBasket(restaurantId: String): List<BasketIdPair>

    fun addBasketChangesListener(listenerOwner: Any, listener: (List<BasketIdPair>) -> Unit)

    /**
     * Must be called before listener destroy to avoid leaks!
     */
    fun clearMyBasketChangesListeners(listenerOwner: Any)
}