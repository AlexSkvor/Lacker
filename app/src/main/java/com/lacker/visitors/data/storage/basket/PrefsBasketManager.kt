package com.lacker.visitors.data.storage.basket

import android.content.Context
import androidx.core.content.edit
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import com.lacker.visitors.data.api.ApiCallResult
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

    override suspend fun sendBasketToServer(): ApiCallResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun addToBasket(
        restaurantId: String,
        portion: BasketIdPair
    ): ApiCallResult<Unit> {
        basketIdPairs = if (this.restaurantId != restaurantId) {
            this.restaurantId = restaurantId
            listOf(portion).also { notifyListeners(it) }
        } else (basketIdPairs + portion).also { notifyListeners(it) }

        return ApiCallResult.Result(Unit)
    }

    override suspend fun removeFromBasket(
        restaurantId: String,
        portion: BasketIdPair
    ): ApiCallResult<Unit> {
        basketIdPairs = if (this.restaurantId != restaurantId) {
            this.restaurantId = restaurantId
            emptyList()
        } else (basketIdPairs - portion).also { notifyListeners(it) }

        return ApiCallResult.Result(Unit)
    }

    override suspend fun getBasket(restaurantId: String): ApiCallResult<List<BasketIdPair>> =
        ApiCallResult.Result(basketIdPairs)

    private var restaurantId: String?
        get() = prefs.getString(RESTAURANT_ID_KEY, null)
        set(value) = prefs.edit { putString(restaurantId, value) }

    private var basketIdPairs: List<BasketIdPair>
        get() = prefs.getString(BASKET_IDS_KEY, null)
            ?.split('|')
            ?.filterNot { it.isBlank() }
            ?.map { BasketIdPair(it.substringBefore('%'), it.substringAfter('%')) }
            .orEmpty()
        set(value) {
            val str = if (value.isEmpty()) null
            else value.joinToString(separator = "|") { "${it.menuItemId}%${it.portionId}" }
            prefs.edit { putString(BASKET_IDS_KEY, str) }
        }

    private suspend fun notifyListeners(newList: List<BasketIdPair>) {
        withContext(Dispatchers.Main) {
            listeners.values.forEach {
                it.forEach { listener -> listener(newList) }
            }
        }
    }

    private val listeners: MutableMap<Any, List<(List<BasketIdPair>) -> Unit>> = mutableMapOf()

    override fun addBasketChangesListener(
        listenerOwner: Any,
        listener: (List<BasketIdPair>) -> Unit
    ) {
        if (listeners[listenerOwner] == null) listeners[listenerOwner] = listOf(listener)
        else listeners[listenerOwner] = listeners[listenerOwner]?.plus(listener).orEmpty()
    }

    override fun clearMyBasketChangesListeners(listenerOwner: Any) {
        listeners.remove(listenerOwner)
    }
}