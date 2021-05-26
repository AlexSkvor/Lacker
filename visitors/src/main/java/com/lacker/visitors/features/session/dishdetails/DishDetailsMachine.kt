package com.lacker.visitors.features.session.dishdetails

import com.lacker.dto.order.Order
import com.lacker.dto.order.OrderInfo
import com.lacker.utils.exceptions.ImpossibleSituationException
import com.lacker.utils.resources.ResourceProvider
import com.lacker.visitors.R
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.api.NetworkManager
import com.lacker.visitors.data.dto.order.AddSuborderRequest
import com.lacker.visitors.data.dto.order.CreateOrderRequest
import com.lacker.visitors.data.storage.basket.BasketManager
import com.lacker.visitors.data.storage.favourite.FavouritesManager
import com.lacker.visitors.data.storage.order.OrderStorage
import com.lacker.visitors.data.storage.session.SessionStorage
import com.lacker.visitors.features.session.common.DomainMenuItem
import com.lacker.visitors.features.session.common.DomainPortion
import javax.inject.Inject
import com.lacker.visitors.features.session.dishdetails.DishDetailsMachine.Wish
import com.lacker.visitors.features.session.dishdetails.DishDetailsMachine.State
import com.lacker.visitors.features.session.dishdetails.DishDetailsMachine.Result
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class DishDetailsMachine @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val basketManager: BasketManager,
    private val favouritesManager: FavouritesManager,
    private val resourceProvider: ResourceProvider,
    private val router: Router,
    private val net: NetworkManager,
    private val orderStorage: OrderStorage,
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        data class SetDishAndOrderId(val dish: DomainMenuItem, val orderId: String) : Wish()

        data class AddToOrder(
            val comment: String,
            val portion: OrderInfo,
            val drinksAsap: Boolean,
        ) : Wish()

        data class AddToBasket(val portion: DomainPortion) : Wish()
        data class RemoveFromBasket(val portion: DomainPortion) : Wish()

        object AddToFavourite : Wish()
        object RemoveFromFavourite : Wish()
    }

    sealed class Result {
        sealed class OrderResult : Result() {
            data class OrderLoaded(val order: Map<String, Int>) : OrderResult()
            data class Error(val text: String) : OrderResult()
        }

        sealed class BasketResult : Result() {
            data class BasketLoaded(val basket: Map<String, Int>) : BasketResult()
            data class Error(val text: String) : BasketResult()
        }

        sealed class FavouriteResult : Result() {
            data class FavouriteLoaded(val favourite: Boolean) : FavouriteResult()
            data class Error(val text: String) : FavouriteResult()
        }
    }

    data class State(
        val loading: Boolean = false,
        val dish: DomainMenuItem? = null,
        val orderId: String? = null,
    )

    override val initialState: State = State()

    private val session by lazy {
        sessionStorage.session
            ?: throw ImpossibleSituationException("User requested dish details without session in SessionStorage")
    }

    private val restaurantId by lazy { session.restaurantId }
    private val tableId by lazy { session.tableId }

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        is Wish.AddToBasket -> oldState.also {
            pushResult { addToBasket(wish.portion.id) }
        }
        is Wish.RemoveFromBasket -> oldState.also {
            pushResult { removeFromBasket(wish.portion.id) }
        }
        is Wish.AddToFavourite -> oldState.also {
            if (oldState.dish != null) pushResult { addToFavourite(oldState.dish.id) }
        }
        is Wish.RemoveFromFavourite -> oldState.also {
            if (oldState.dish != null) pushResult { removeFromFavourite(oldState.dish.id) }
        }
        is Wish.AddToOrder -> oldState.copy(loading = true).also {
            sendMessage(resourceProvider.getString(R.string.requestSent))
            pushResult { addToOrder(wish.comment, wish.portion, wish.drinksAsap, it.orderId) }
        }
        is Wish.SetDishAndOrderId -> oldState.copy(dish = wish.dish, orderId = wish.orderId)
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        is Result.BasketResult.BasketLoaded -> {
            val portions = oldState.dish?.portions.orEmpty()
                .map { it.copy(basketNumber = res.basket.getOrDefault(it.id, 0)) }
            oldState.copy(dish = oldState.dish?.copy(portions = portions))
        }
        is Result.BasketResult.Error -> oldState.also { sendMessage(res.text) }
        is Result.FavouriteResult.FavouriteLoaded -> oldState.copy(
            dish = oldState.dish?.copy(inFavourites = res.favourite)
        )
        is Result.FavouriteResult.Error -> oldState.also { sendMessage(res.text) }
        is Result.OrderResult.OrderLoaded -> {
            val portions = oldState.dish?.portions.orEmpty()
                .map { it.copy(orderedNumber = res.order.getOrDefault(it.id, 0)) }
            oldState.copy(loading = false, dish = oldState.dish?.copy(portions = portions))
        }
        is Result.OrderResult.Error -> oldState.copy(loading = false).also { sendMessage(res.text) }
    }

    private suspend fun addToBasket(portionId: String): Result.BasketResult {
        val possiblePortionIds = states().value.dish?.portions?.map { it.id }.orEmpty()

        return when (val res = basketManager.addToBasket(restaurantId, portionId)) {
            is ApiCallResult.Result -> {
                val map = res.value
                    .filter { it.portionId in possiblePortionIds }
                    .map { it.portionId to it.ordered }
                    .toMap()
                Result.BasketResult.BasketLoaded(map)
            }
            is ApiCallResult.ErrorOccurred -> Result.BasketResult.Error(res.text)
        }
    }

    private suspend fun removeFromBasket(portionId: String): Result.BasketResult {
        val possiblePortionIds = states().value.dish?.portions?.map { it.id }.orEmpty()

        return when (val res = basketManager.removeFromBasket(restaurantId, portionId)) {
            is ApiCallResult.Result -> {
                val map = res.value
                    .filter { it.portionId in possiblePortionIds }
                    .map { it.portionId to it.ordered }
                    .toMap()
                Result.BasketResult.BasketLoaded(map)
            }
            is ApiCallResult.ErrorOccurred -> Result.BasketResult.Error(res.text)
        }
    }

    private suspend fun addToFavourite(dishId: String): Result.FavouriteResult {
        return when (val res = favouritesManager.addToFavourites(restaurantId, dishId)) {
            is ApiCallResult.Result -> Result.FavouriteResult.FavouriteLoaded(dishId in res.value)
            is ApiCallResult.ErrorOccurred -> Result.FavouriteResult.Error(res.text)
        }
    }

    private suspend fun removeFromFavourite(dishId: String): Result.FavouriteResult {
        return when (val res = favouritesManager.removeFromFavourites(restaurantId, dishId)) {
            is ApiCallResult.Result -> Result.FavouriteResult.FavouriteLoaded(dishId in res.value)
            is ApiCallResult.ErrorOccurred -> Result.FavouriteResult.Error(res.text)
        }
    }

    private suspend fun addToOrder(
        comment: String,
        order: OrderInfo,
        drinksAsap: Boolean,
        orderId: String?,
    ): Result.OrderResult {
        val request = AddSuborderRequest(
            comment = comment,
            drinksImmediately = drinksAsap,
            portions = List(order.ordered) { order.portionId }
        )

        if (orderId == null || orderId.isEmpty()) return when (
            val res = net.callResult { createOrder(CreateOrderRequest(tableId, request)) }
        ) {
            is ApiCallResult.Result -> Result.OrderResult.OrderLoaded(res.value.order.orderedNumber())
            is ApiCallResult.ErrorOccurred -> Result.OrderResult.Error(res.text)
        }

        return when (val res = net.callResult { addToCurrentOrder(orderId, request) }) {
            is ApiCallResult.Result -> Result.OrderResult.OrderLoaded(res.value.order.orderedNumber())
            is ApiCallResult.ErrorOccurred -> Result.OrderResult.Error(resourceProvider.getString(R.string.orderClosed))
        }
    }

    private fun Order.orderedNumber(): Map<String, Int> {
        orderStorage.orderId = id
        val map = mutableMapOf<String, Int>()
        val possiblePortionIds = states().value.dish?.portions?.map { it.id }.orEmpty()
        subOrders.map { it.orderList }.flatten()
            .filter { it.portionId in possiblePortionIds }
            .forEach {
                map[it.portionId] = map.getOrDefault(it.portionId, 0) + it.ordered
            }
        return map
    }

    override fun onBackPressed() {
        if (!states().value.loading) router.exit()
    }
}