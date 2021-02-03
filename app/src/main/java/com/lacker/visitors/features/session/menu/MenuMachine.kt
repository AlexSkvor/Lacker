package com.lacker.visitors.features.session.menu

import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.dto.menu.MenuItem
import com.lacker.visitors.data.dto.menu.OrderInfo
import com.lacker.visitors.data.dto.menu.toDomain
import com.lacker.visitors.data.storage.basket.BasketManager
import com.lacker.visitors.data.storage.menu.MenuManager
import com.lacker.visitors.data.storage.session.SessionStorage
import voodoo.rocks.flux.Machine
import javax.inject.Inject
import com.lacker.visitors.features.session.menu.MenuMachine.Wish
import com.lacker.visitors.features.session.menu.MenuMachine.State
import com.lacker.visitors.features.session.menu.MenuMachine.Result
import com.lacker.visitors.utils.ImpossibleSituationException
import kotlinx.coroutines.delay
import kotlin.random.Random

class MenuMachine @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val menuManager: MenuManager,
    private val basketManager: BasketManager
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        object Refresh : Wish()

        data class AddToOrder(val portion: DomainPortion) : Wish()
        data class AddToBasket(val portion: DomainPortion) : Wish()
        data class RemoveFromBasket(val portion: DomainPortion) : Wish()
    }

    sealed class Result {

        sealed class OrderResult : Result() {
            data class Order(val order: List<OrderInfo>) : OrderResult()
            data class Error(val text: String) : OrderResult()
        }

        sealed class MenuResult : Result() {
            data class Menu(val items: List<MenuItem>) : MenuResult()
            data class Error(val text: String) : MenuResult()
        }

        sealed class BasketResult : Result() {
            data class Basket(val basket: List<OrderInfo>) : BasketResult()
            data class Error(val text: String) : BasketResult()
        }

    }

    data class State(
        val orderLoading: Boolean = false,
        val menuLoading: Boolean = false,
        val basketLoading: Boolean = false,
        val order: List<OrderInfo>? = null,
        val menuItems: List<MenuItem>? = null,
        val basket: List<OrderInfo>? = null,
        val menuWithOrders: List<DomainMenuItem>? = null,
        val errorText: String? = null
    ) {
        val empty = menuWithOrders == null
        val showLoading = (orderLoading || menuLoading || basketLoading)
    }

    override val initialState: State = State()

    private val restaurantId by lazy {
        sessionStorage.session?.restaurantId
            ?: throw ImpossibleSituationException("User requested menu without restaurantId in SessionStorage")
    }

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        Wish.Refresh -> oldState.copy(
            orderLoading = true,
            menuLoading = true,
            basketLoading = true,
            errorText = null
        ).also {
                pushResult { loadMenu() }
                pushResult { loadOrder() }
                pushResult { loadBasket() }
            }
        is Wish.AddToOrder -> TODO("Create OrderManager and AuthChecker!")
        is Wish.AddToBasket -> oldState.also { pushResult { addToBasket(wish.portion) } }
        is Wish.RemoveFromBasket -> oldState.also { pushResult { removeFromBasket(wish.portion) } }
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        is Result.OrderResult.Order -> oldState.copy(orderLoading = false, order = res.order)
            .recountMenuWithOrdersAndBasket()
        is Result.OrderResult.Error -> oldState.copy(
            orderLoading = false,
            errorText = if (oldState.order == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
        is Result.MenuResult.Menu -> oldState.copy(menuLoading = false, menuItems = res.items)
            .recountMenuWithOrdersAndBasket()
        is Result.MenuResult.Error -> oldState.copy(
            menuLoading = false,
            errorText = if (oldState.menuItems == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
        is Result.BasketResult.Basket -> oldState.copy(basketLoading = false, basket = res.basket)
            .recountMenuWithOrdersAndBasket()
        is Result.BasketResult.Error -> oldState.copy(
            basketLoading = false,
            errorText = if (oldState.basket == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
    }

    private fun State.recountMenuWithOrdersAndBasket(): State {
        if (orderLoading || menuLoading || basketLoading) return this
        if (order == null || menuItems == null || basket == null) return copy(menuWithOrders = null)

        return copy(errorText = null, menuWithOrders = menuItems.map { it.toDomain(order, basket) })
    }

    private suspend fun loadMenu(): Result.MenuResult {
        return when (val res = menuManager.getMenu(restaurantId)) {
            is ApiCallResult.Result -> Result.MenuResult.Menu(res.value)
            is ApiCallResult.ErrorOccurred -> Result.MenuResult.Error(res.text)
        }
    }

    private suspend fun loadBasket(): Result.BasketResult {
        return when (val res = basketManager.getBasket(restaurantId)) {
            is ApiCallResult.Result -> Result.BasketResult.Basket(res.value)
            is ApiCallResult.ErrorOccurred -> Result.BasketResult.Error(res.text)
        }
    }

    private suspend fun addToBasket(portion: DomainPortion): Result.BasketResult {
        return when (val res = basketManager.addToBasket(restaurantId, portion.id)) {
            is ApiCallResult.Result -> Result.BasketResult.Basket(res.value)
            is ApiCallResult.ErrorOccurred -> Result.BasketResult.Error(res.text)
        }
    }

    private suspend fun removeFromBasket(portion: DomainPortion): Result.BasketResult {
        return when (val res = basketManager.removeFromBasket(restaurantId, portion.id)) {
            is ApiCallResult.Result -> Result.BasketResult.Basket(res.value)
            is ApiCallResult.ErrorOccurred -> Result.BasketResult.Error(res.text)
        }
    }

    private suspend fun loadOrder(): Result.OrderResult {
        delay(Random.nextLong(100, 2000))
        return Result.OrderResult.Order(emptyList())
        //TODO Just stub here, rework when OrderManager appear!
    }

    override fun onBackPressed() {
        // TODO Tap twice to exit
    }
}