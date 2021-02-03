package com.lacker.visitors.features.session.basket

import com.lacker.utils.resources.ResourceProvider
import com.lacker.visitors.R
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.dto.menu.MenuItem
import com.lacker.visitors.data.dto.menu.OrderInfo
import com.lacker.visitors.data.dto.menu.toDomain
import com.lacker.visitors.data.storage.basket.BasketManager
import com.lacker.visitors.data.storage.menu.MenuManager
import com.lacker.visitors.data.storage.session.SessionStorage
import javax.inject.Inject
import com.lacker.visitors.features.session.basket.BasketMachine.Wish
import com.lacker.visitors.features.session.basket.BasketMachine.State
import com.lacker.visitors.features.session.basket.BasketMachine.Result
import com.lacker.visitors.features.session.common.DomainPortion
import com.lacker.visitors.features.session.common.MenuAdapterItem
import com.lacker.visitors.features.session.common.MenuButtonItem
import com.lacker.visitors.navigation.Screens
import com.lacker.visitors.utils.ImpossibleSituationException
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class BasketMachine @Inject constructor(
    private val router: Router,
    private val sessionStorage: SessionStorage,
    private val menuManager: MenuManager,
    private val basketManager: BasketManager,
    private val resourceProvider: ResourceProvider
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        object Refresh : Wish()
        object SendBasketToServer : Wish()

        data class AddToOrder(val portion: DomainPortion) : Wish()
        data class AddToBasket(val portion: DomainPortion) : Wish()
        data class RemoveFromBasket(val portion: DomainPortion) : Wish()
    }

    sealed class Result {
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
        val menuLoading: Boolean = false,
        val basketLoading: Boolean = false,
        val menuItems: List<MenuItem>? = null,
        val basket: List<OrderInfo>? = null,
        val menuWithBasket: List<MenuAdapterItem>? = null,
        val errorText: String? = null
    ) {
        val empty = menuWithBasket == null
        val showLoading = menuLoading || basketLoading
    }

    private val restaurantId by lazy {
        sessionStorage.session?.restaurantId
            ?: throw ImpossibleSituationException("User requested basket without restaurantId in SessionStorage")
    }

    private val startCookingItem by lazy {
        MenuButtonItem(resourceProvider.getString(R.string.startCooking), Wish.SendBasketToServer)
    }

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        Wish.Refresh -> oldState.copy(
            menuLoading = true,
            basketLoading = true,
            errorText = null
        ).also {
            pushResult { loadMenu() }
            pushResult { loadBasket() }
        }
        is Wish.AddToBasket -> oldState.also { pushResult { addToBasket(wish.portion) } }
        is Wish.RemoveFromBasket -> oldState.also { pushResult { removeFromBasket(wish.portion) } }
        is Wish.AddToOrder -> TODO("Create OrderManager and AuthChecker!")
        Wish.SendBasketToServer -> TODO("Create OrderManager and AuthChecker!")
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        is Result.MenuResult.Menu -> oldState.copy(menuLoading = false, menuItems = res.items)
            .recountMenuWithBasket()
        is Result.MenuResult.Error -> oldState.copy(
            menuLoading = false,
            errorText = if (oldState.menuItems == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
        is Result.BasketResult.Basket -> oldState.copy(basketLoading = false, basket = res.basket)
            .recountMenuWithBasket()
        is Result.BasketResult.Error -> oldState.copy(
            basketLoading = false,
            errorText = if (oldState.basket == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
    }

    private fun State.recountMenuWithBasket(): State {
        if (menuLoading || basketLoading) return this
        if (menuItems == null || basket == null) return copy(menuWithBasket = null)

        val basketPortionIds = basket.map { it.portionId }

        //TODO empty placeholder if basket is empty
        val menu = menuItems
            .filter { it.portions.map { p -> p.id }.any { pId -> pId in basketPortionIds } }
            .map { it.toDomain(emptyList(), basket) }
            .let { if (it.isEmpty()) emptyList() else it.plus(startCookingItem) }

        return copy(errorText = null, menuWithBasket = menu)
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

    override fun onBackPressed() {
        router.backTo(Screens.MenuScreen)
    }
}