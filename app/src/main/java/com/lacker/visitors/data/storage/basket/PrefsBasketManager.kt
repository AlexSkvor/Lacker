package com.lacker.visitors.data.storage.basket

import android.content.Context
import androidx.core.content.edit
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.storage.basket.BasketManager.Companion.MAX_BASKET_SIZE_FOR_ONE_MENU_ITEM
import com.lacker.visitors.data.dto.menu.OrderInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Math.min
import java.lang.Math.max
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
        portionId: String
    ): ApiCallResult<List<OrderInfo>> {

        if (this.restaurantId != restaurantId) {
            this.restaurantId = restaurantId
            basket = emptyList()
        }

        val oldBasket = basket

        val oldInfo = oldBasket.find { it.portionId == portionId }

        basket = if (oldInfo == null) oldBasket + OrderInfo(portionId, 1)
        else {
            val newInfo = oldInfo.copy(
                ordered = min(
                    oldInfo.ordered + 1,
                    MAX_BASKET_SIZE_FOR_ONE_MENU_ITEM
                )
            )
            oldBasket.map { if (it.portionId == portionId) newInfo else it }
        }

        return ApiCallResult.Result(basket.also { notifyListeners(it) })
    }

    override suspend fun removeFromBasket(
        restaurantId: String,
        portionId: String
    ): ApiCallResult<List<OrderInfo>> {

        if (this.restaurantId != restaurantId) {
            this.restaurantId = restaurantId
            basket = emptyList()
        }


        val oldBasket = basket

        val oldInfo = oldBasket.find { it.portionId == portionId }

        basket = if (oldInfo == null) oldBasket
        else {
            val newInfo = oldInfo.copy(ordered = max(oldInfo.ordered - 1, 0))
            oldBasket.map { if (it.portionId == portionId) newInfo else it }
        }

        return ApiCallResult.Result(basket.also { notifyListeners(it) })
    }

    override suspend fun getBasket(restaurantId: String): ApiCallResult<List<OrderInfo>> =
        ApiCallResult.Result(basket)

    private var restaurantId: String?
        get() = prefs.getString(RESTAURANT_ID_KEY, null)
        set(value) = prefs.edit { putString(RESTAURANT_ID_KEY, value) }

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