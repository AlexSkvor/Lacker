package com.lacker.visitors.features.session.dishdetails

import com.lacker.utils.resources.ResourceProvider
import com.lacker.visitors.R
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.api.NetworkManager
import com.lacker.visitors.data.dto.menu.OrderInfo
import com.lacker.visitors.data.dto.order.SubOrder
import com.lacker.visitors.data.storage.basket.BasketManager
import com.lacker.visitors.data.storage.favourite.FavouritesManager
import com.lacker.visitors.data.storage.session.SessionStorage
import com.lacker.visitors.features.session.common.DomainMenuItem
import com.lacker.visitors.features.session.common.DomainPortion
import javax.inject.Inject
import com.lacker.visitors.features.session.dishdetails.DishDetailsMachine.Wish
import com.lacker.visitors.features.session.dishdetails.DishDetailsMachine.State
import com.lacker.visitors.features.session.dishdetails.DishDetailsMachine.Result
import com.lacker.visitors.utils.ImpossibleSituationException
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine
import java.time.OffsetDateTime

class DishDetailsMachine @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val basketManager: BasketManager,
    private val favouritesManager: FavouritesManager,
    private val resourceProvider: ResourceProvider,
    private val router: Router,
    private val net: NetworkManager
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        data class SetDish(val dish: DomainMenuItem) : Wish()

        data class AddToOrder(val comment: String, val portion: OrderInfo) : Wish()

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
        val dish: DomainMenuItem? = null,
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
        is Wish.AddToOrder -> oldState.also {
            sendMessage(resourceProvider.getString(R.string.requestSent))
            pushResult { addToOrder(wish.comment, wish.portion) }
        }
        is Wish.SetDish -> oldState.copy(dish = wish.dish)
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
            oldState.copy(dish = oldState.dish?.copy(portions = portions))
        }
        is Result.OrderResult.Error -> oldState.also { sendMessage(res.text) }
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

    private suspend fun addToOrder(comment: String, info: OrderInfo): Result.OrderResult {
        val subOrder = SubOrder(
            comment = comment,
            drinksImmediately = true,
            orderList = listOf(info),
            createdTimeStamp = OffsetDateTime.now()
        )

        val possiblePortionIds = states().value.dish?.portions?.map { it.id }.orEmpty()
        val res = net.callResult { addToCurrentOrder(restaurantId, tableId, subOrder) }
        return when (res) {
            is ApiCallResult.Result -> {
                val map = res.value.order?.subOrders.orEmpty()
                    .map { it.orderList }.flatten()
                    .filter { it.portionId in possiblePortionIds }
                    .map { it.portionId to it.ordered }
                    .toMap()
                Result.OrderResult.OrderLoaded(map)
            }
            is ApiCallResult.ErrorOccurred -> Result.OrderResult.Error(res.text)
        }
    }

    override fun onBackPressed() {
        router.exit()
    }
}