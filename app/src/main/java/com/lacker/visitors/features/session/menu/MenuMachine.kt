package com.lacker.visitors.features.session.menu

import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.api.NetworkManager
import com.lacker.visitors.data.dto.menu.MenuItem
import com.lacker.visitors.data.dto.menu.OrderInfo
import com.lacker.visitors.data.dto.menu.toDomain
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
    private val net: NetworkManager
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        object Refresh : Wish()

        data class AddToOrder(val portion: DomainPortion) : Wish()
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

    }

    data class State(
        val orderLoading: Boolean = false,
        val menuLoading: Boolean = false,
        val order: List<OrderInfo>? = null,
        val menuItems: List<MenuItem>? = null,
        val menuWithOrders: List<DomainMenuItem>? = null,
        val errorText: String? = null
    ) {
        val empty = menuWithOrders == null
        val showLoading = (orderLoading || menuLoading)
    }

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        Wish.Refresh -> oldState.copy(orderLoading = true, menuLoading = true).also {
            pushResult { loadMenu() }
            pushResult { loadOrder() }
        }
        is Wish.AddToOrder -> TODO("Create OrderManager and AuthChecker!")
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        is Result.OrderResult.Order -> oldState.copy(orderLoading = false, order = res.order)
            .recountMenuWithOrders()
        is Result.OrderResult.Error -> oldState.copy(
            orderLoading = false,
            errorText = if (oldState.order == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
        is Result.MenuResult.Menu -> oldState.copy(menuLoading = false, menuItems = res.items)
            .recountMenuWithOrders()
        is Result.MenuResult.Error -> oldState.copy(
            menuLoading = false,
            errorText = if (oldState.menuItems == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
    }

    private fun State.recountMenuWithOrders(): State {
        if (orderLoading || menuLoading) return this
        if (order == null || menuItems == null) return copy(menuWithOrders = null)

        return copy(errorText = null, menuWithOrders = menuItems.map { it.toDomain(order) })
    }

    private suspend fun loadMenu(): Result.MenuResult {

        val restaurantId = sessionStorage.session?.restaurantId
            ?: throw ImpossibleSituationException("User requested menu without restaurantId in SessionStorage")

        return when (val res = menuManager.getMenu(restaurantId)) {
            is ApiCallResult.Result -> Result.MenuResult.Menu(res.value)
            is ApiCallResult.ErrorOccurred -> Result.MenuResult.Error(res.text)
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