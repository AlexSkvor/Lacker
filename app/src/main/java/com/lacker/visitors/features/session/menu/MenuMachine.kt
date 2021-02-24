package com.lacker.visitors.features.session.menu

import com.lacker.utils.extensions.onNull
import com.lacker.utils.resources.ResourceProvider
import com.lacker.visitors.R
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.dto.menu.MenuItem
import com.lacker.visitors.data.dto.menu.OrderInfo
import com.lacker.visitors.data.dto.menu.toDomain
import com.lacker.visitors.data.storage.basket.BasketManager
import com.lacker.visitors.data.storage.favourite.FavouritesManager
import com.lacker.visitors.data.storage.menu.MenuManager
import com.lacker.visitors.data.storage.session.SessionStorage
import com.lacker.visitors.features.session.common.DomainPortion
import com.lacker.visitors.features.session.common.MenuAdapterItem
import com.lacker.visitors.features.session.common.MenuButtonItem
import voodoo.rocks.flux.Machine
import javax.inject.Inject
import com.lacker.visitors.features.session.menu.MenuMachine.Wish
import com.lacker.visitors.features.session.menu.MenuMachine.State
import com.lacker.visitors.features.session.menu.MenuMachine.Result
import com.lacker.visitors.utils.ImpossibleSituationException
import kotlinx.coroutines.delay
import ru.terrakok.cicerone.Router
import kotlin.random.Random

class MenuMachine @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val menuManager: MenuManager,
    private val basketManager: BasketManager,
    private val favouritesManager: FavouritesManager,
    private val resourceProvider: ResourceProvider,
    private val router: Router
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        object Refresh : Wish()

        data class AddToOrder(val portion: DomainPortion) : Wish()
        data class AddToBasket(val portion: DomainPortion) : Wish()
        data class AddToFavourite(val menuItemId: String) : Wish()
        data class RemoveFromBasket(val portion: DomainPortion) : Wish()
        data class RemoveFromFavourite(val menuItemId: String) : Wish()

        data class ChangeComment(val comment: String) : Wish()

        data class ChangeShowType(val type: State.Type) : Wish()
        object SendBasketToServer : Wish()
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

        sealed class FavouriteResult : Result() {
            data class Favourites(val items: Set<String>) : FavouriteResult()
            data class Error(val text: String) : FavouriteResult()
        }

    }

    data class State(
        val orderLoading: Boolean = false,
        val menuLoading: Boolean = false,
        val basketLoading: Boolean = false,
        val favouritesLoading: Boolean = false,
        val order: List<OrderInfo>? = null,
        val menuItems: List<MenuItem>? = null,
        val basket: List<OrderInfo>? = null,
        val favourites: Set<String>? = null,
        val menuShowList: List<MenuAdapterItem>? = null,
        val basketShowList: List<MenuAdapterItem>? = null,
        val favouriteShowList: List<MenuAdapterItem>? = null,
        val orderShowList: List<MenuAdapterItem>? = null,
        val errorText: String? = null,
        val type: Type = Type.MENU,
        val comment: String = "",
        val drinksImmediately: Boolean = false // TODO use it on order dialog
    ) {

        val showList = when (type) {
            Type.MENU -> menuShowList
            Type.FAVOURITE -> favouriteShowList
            Type.BASKET -> basketShowList
            Type.ORDER -> orderShowList
        }

        val empty = showList.isNullOrEmpty()
        val showLoading = (orderLoading || menuLoading || basketLoading || favouritesLoading)

        enum class Type {
            MENU, FAVOURITE, BASKET, ORDER
        }
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
            favouritesLoading = true,
            errorText = null
        ).also {
            pushResult { loadMenu() }
            pushResult { loadOrder() }
            pushResult { loadBasket() }
            pushResult { loadFavourites() }
        }
        is Wish.AddToOrder -> TODO("Create OrderManager and AuthChecker!")
        is Wish.AddToBasket -> oldState.also { pushResult { addToBasket(wish.portion) } }
        is Wish.AddToFavourite -> oldState.also { pushResult { addToFavourites(wish.menuItemId) } }
        is Wish.RemoveFromBasket -> oldState.also { pushResult { removeFromBasket(wish.portion) } }
        is Wish.RemoveFromFavourite -> oldState.also { pushResult { removeFromFavourites(wish.menuItemId) } }
        is Wish.ChangeShowType -> oldState.copy(type = wish.type)
        Wish.SendBasketToServer -> TODO("Create OrderManager and AuthChecker!")
        // add filter for OrderLoading!
        is Wish.ChangeComment -> oldState.copy(comment = wish.comment)
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        is Result.OrderResult.Order -> oldState.copy(orderLoading = false, order = res.order)
            .recountMenuWithOrdersAndBasketAndFavourites()
        is Result.OrderResult.Error -> oldState.copy(
            orderLoading = false,
            errorText = if (oldState.order == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
        is Result.MenuResult.Menu -> oldState.copy(menuLoading = false, menuItems = res.items)
            .recountMenuWithOrdersAndBasketAndFavourites()
        is Result.MenuResult.Error -> oldState.copy(
            menuLoading = false,
            errorText = if (oldState.menuItems == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
        is Result.BasketResult.Basket -> oldState.copy(basketLoading = false, basket = res.basket)
            .recountMenuWithOrdersAndBasketAndFavourites()
        is Result.BasketResult.Error -> oldState.copy(
            basketLoading = false,
            errorText = if (oldState.basket == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
        is Result.FavouriteResult.Favourites -> oldState.copy(
            favouritesLoading = false,
            favourites = res.items
        ).recountMenuWithOrdersAndBasketAndFavourites()
        is Result.FavouriteResult.Error -> oldState.copy(
            favouritesLoading = false,
            errorText = if (oldState.favourites == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
    }

    private val startCookingItem by lazy {
        MenuButtonItem(resourceProvider.getString(R.string.startCooking), Wish.SendBasketToServer)
    }

    private fun State.recountMenuWithOrdersAndBasketAndFavourites(): State {
        if (orderLoading || menuLoading || basketLoading || favouritesLoading) return this
        if (order == null || menuItems == null || basket == null || favourites == null) return copy(
            menuShowList = null,
            basketShowList = null,
            orderShowList = null,
            favouriteShowList = null
        )

        val menuTmp = menuItems.map { it.toDomain(order, basket, favourites) }
        val basketTmp = menuTmp.filter { it.portions.any { p -> p.basketNumber > 0 } }
            .let { if (it.isEmpty()) emptyList() else it.plus(startCookingItem) }

        return copy(
            errorText = null,
            menuShowList = menuTmp,
            basketShowList = basketTmp,
            orderShowList = emptyList(), // TODO
            favouriteShowList = menuTmp.filter { it.inFavourites }
        )
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

    private suspend fun loadFavourites(): Result.FavouriteResult {
        return when (val res = favouritesManager.getFavourites(restaurantId)) {
            is ApiCallResult.Result -> Result.FavouriteResult.Favourites(res.value)
            is ApiCallResult.ErrorOccurred -> Result.FavouriteResult.Error(res.text)
        }
    }

    private suspend fun addToFavourites(menuItemId: String): Result.FavouriteResult {
        return when (val res = favouritesManager.addToFavourites(restaurantId, menuItemId)) {
            is ApiCallResult.Result -> Result.FavouriteResult.Favourites(res.value)
            is ApiCallResult.ErrorOccurred -> Result.FavouriteResult.Error(res.text)
        }
    }

    private suspend fun removeFromFavourites(menuItemId: String): Result.FavouriteResult {
        return when (val res = favouritesManager.removeFromFavourites(restaurantId, menuItemId)) {
            is ApiCallResult.Result -> Result.FavouriteResult.Favourites(res.value)
            is ApiCallResult.ErrorOccurred -> Result.FavouriteResult.Error(res.text)
        }
    }

    private suspend fun loadOrder(): Result.OrderResult {
        delay(Random.nextLong(100, 2000))
        return Result.OrderResult.Order(emptyList())
        //TODO Just stub here, rework when OrderManager appear!
    }

    private var lastClickTime: Long? = null
    override fun onBackPressed() {
        val now = System.currentTimeMillis()
        if (lastClickTime == null || now - lastClickTime.onNull(0) > 2000) {
            lastClickTime = now
            sendMessage(resourceProvider.getString(R.string.clickAgainToCloseApp))
        } else router.exit()
    }
}