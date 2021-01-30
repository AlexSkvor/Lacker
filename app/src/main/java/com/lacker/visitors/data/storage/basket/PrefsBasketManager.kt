package com.lacker.visitors.data.storage.basket

import android.content.Context
import androidx.core.content.edit
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.dto.menu.OrderInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PrefsBasketManager @Inject constructor(
    private val context: Context
) : BasketManager {

    private companion object {
        const val RESTAURANT_ID_KEY = "PrefsBasketManager RESTAURANT_ID_KEY"
        const val BASKET_IDS_KEY = "PrefsBasketManager BASKET_IDS_KEY"
    }

    private val prefs: Preferences by lazy {
        BinaryPreferencesBuilder(context)
            .name("Läcker_visitors_prefs_basket_manager")
            .allowBuildOnBackgroundThread()
            .supportInterProcess(true)
            .build()
    }

    override suspend fun sendBasketToServer(): ApiCallResult<List<OrderInfo>> {
        TODO("Not yet implemented")
    }

    override suspend fun addToBasket(
        restaurantId: String,
        portion: OrderInfo
    ): ApiCallResult<List<OrderInfo>> {
        basket = if (this.restaurantId != restaurantId) {
            this.restaurantId = restaurantId
            listOf(portion).also { notifyListeners(it) }
        } else (basket + portion).also { notifyListeners(it) }

        return ApiCallResult.Result(basket)
    }

    override suspend fun removeFromBasket(
        restaurantId: String,
        portion: OrderInfo
    ): ApiCallResult<List<OrderInfo>> {
        basket = if (this.restaurantId != restaurantId) {
            this.restaurantId = restaurantId
            emptyList()
        } else (basket - portion).also { notifyListeners(it) }

        return ApiCallResult.Result(basket)
    }

    override suspend fun getBasket(restaurantId: String): ApiCallResult<List<OrderInfo>> =
        ApiCallResult.Result(basket)

    private var restaurantId: String?
        get() = prefs.getString(RESTAURANT_ID_KEY, null)
        set(value) = prefs.edit { putString(restaurantId, value) }

    private var basket: List<OrderInfo>
        get() = prefs.getString(BASKET_IDS_KEY, null)
            ?.split('|')
            ?.filterNot { it.isBlank() }
            ?.map { OrderInfo(it.substringBefore('%'), it.substringAfter('%').toInt()) }
            .orEmpty()
        set(value) {
            val str = if (value.isEmpty()) null
            else value.joinToString(separator = "|") { "${it.portionId}%${it.ordered}" }
            prefs.edit { putString(BASKET_IDS_KEY, str) }
        }

    private suspend fun notifyListeners(newList: List<OrderInfo>) {
        withContext(Dispatchers.Main) {
            listeners.values.forEach {
                it.forEach { listener -> listener(newList) }
            }
        }
    }

    private val listeners: MutableMap<Any, List<(List<OrderInfo>) -> Unit>> = mutableMapOf()

    override fun addBasketChangesListener(
        listenerOwner: Any,
        listener: (List<OrderInfo>) -> Unit
    ) {
        if (listeners[listenerOwner] == null) listeners[listenerOwner] = listOf(listener)
        else listeners[listenerOwner] = listeners[listenerOwner]?.plus(listener).orEmpty()
    }

    override fun clearMyBasketChangesListeners(listenerOwner: Any) {
        listeners.remove(listenerOwner)
    }
}