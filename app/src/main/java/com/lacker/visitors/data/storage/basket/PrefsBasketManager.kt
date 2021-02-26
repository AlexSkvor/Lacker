package com.lacker.visitors.data.storage.basket

import android.content.Context
import androidx.core.content.edit
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.storage.basket.BasketManager.Companion.MAX_BASKET_SIZE_FOR_ONE_MENU_ITEM
import com.lacker.visitors.data.dto.menu.OrderInfo
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

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

    override suspend fun clearBasket(): ApiCallResult<List<OrderInfo>> {
        restaurantId = null
        basket = emptyList()
        return ApiCallResult.Result(basket)
    }

    override suspend fun addToBasket(
        restaurantId: String,
        vararg portionIds: String
    ): ApiCallResult<List<OrderInfo>> {

        if (this.restaurantId != restaurantId) {
            this.restaurantId = restaurantId
            basket = emptyList()
        }

        basket = basket.addPortions(*portionIds)

        return ApiCallResult.Result(basket)
    }

    private fun List<OrderInfo>.addPortions(vararg portionIds: String): List<OrderInfo> {
        var newList = this.toList()
        portionIds.forEach { portionId ->
            val oldInfo = newList.find { it.portionId == portionId }
            newList = if (oldInfo == null) newList + OrderInfo(portionId, 1)
            else {
                val newInfo = oldInfo.copy(
                    ordered = min(oldInfo.ordered + 1, MAX_BASKET_SIZE_FOR_ONE_MENU_ITEM)
                )
                newList.map { if (it.portionId == portionId) newInfo else it }
            }
        }
        return newList
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
                .filter { it.ordered > 0 }
        }

        return ApiCallResult.Result(basket)
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
}