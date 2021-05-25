package com.lacker.visitors.features.session.menu

import com.lacker.dto.menu.MenuItem
import com.lacker.dto.menu.MenuSearchFilter
import com.lacker.dto.order.Order
import com.lacker.dto.order.OrderInfo
import com.lacker.dto.order.SubOrder
import com.lacker.utils.exceptions.ImpossibleSituationException
import com.lacker.utils.extensions.onNull
import com.lacker.utils.resources.ResourceProvider
import com.lacker.visitors.R
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.api.NetworkManager
import com.lacker.visitors.data.dto.menu.toDomain
import com.lacker.visitors.data.dto.order.AddSuborderRequest
import com.lacker.visitors.data.dto.order.CreateOrderRequest
import com.lacker.visitors.data.storage.basket.BasketManager
import com.lacker.visitors.data.storage.favourite.FavouritesManager
import com.lacker.visitors.data.storage.menu.MenuManager
import com.lacker.visitors.data.storage.order.OrderStorage
import com.lacker.visitors.data.storage.session.SessionStorage
import com.lacker.visitors.features.session.common.*
import voodoo.rocks.flux.Machine
import javax.inject.Inject
import com.lacker.visitors.features.session.menu.MenuMachine.Wish
import com.lacker.visitors.features.session.menu.MenuMachine.State
import com.lacker.visitors.features.session.menu.MenuMachine.Result
import com.lacker.visitors.navigation.Screens
import ru.terrakok.cicerone.Router

class MenuMachine @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val menuManager: MenuManager,
    private val basketManager: BasketManager,
    private val favouritesManager: FavouritesManager,
    private val resourceProvider: ResourceProvider,
    private val router: Router,
    private val net: NetworkManager,
    private val orderStorage: OrderStorage,
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        object Refresh : Wish()

        data class AddToOrder(
            val comment: String,
            val portion: OrderInfo,
            val drinksImmediately: Boolean,
        ) : Wish()

        data class AddToBasket(val portion: DomainPortion) : Wish()
        data class AddToFavourite(val menuItemId: String) : Wish()
        data class RemoveFromBasket(val portion: DomainPortion) : Wish()
        data class RemoveFromFavourite(val menuItemId: String) : Wish()

        data class ChangeComment(val comment: String) : Wish()
        data class DrinksImmediatelyChanged(val value: Boolean) : Wish()

        data class ChangeShowType(val type: State.Type) : Wish()
        object SendBasketToServer : Wish()

        data class ShowDishDetails(val dish: DomainMenuItem) : Wish()

        data class SetFilter(val filter: MenuSearchFilter) : Wish()

        object CloseOrder : Wish()
    }

    sealed class Result {

        sealed class OrderResult : Result() {
            data class OrderLoaded(val order: Order?) : OrderResult()
            object OrderNotCreated : OrderResult()
            data class Error(
                val restoreBasket: List<OrderInfo>?,
                val restoreComment: String?,
                val restoreDrinksFlag: Boolean?,
                val text: String
            ) : OrderResult()
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

        sealed class CloseOrder : Result() {
            object Success : CloseOrder()
            data class Error(val text: String) : CloseOrder()
        }

    }

    data class State(
        val orderLoading: Boolean = false,
        val menuLoading: Boolean = false,
        val basketLoading: Boolean = false,
        val favouritesLoading: Boolean = false,
        val order: Order? = null,
        val subOrders: List<SubOrder>? = null,
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
        val drinksImmediately: Boolean = false,
        val filter: MenuSearchFilter? = null,
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

    private val session by lazy {
        sessionStorage.session
            ?: throw ImpossibleSituationException("User requested menu without session in SessionStorage")
    }

    private val restaurantId by lazy { session.restaurantId }
    private val tableId by lazy { session.tableId }

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        Wish.Refresh -> oldState.copy(
            orderLoading = true,
            menuLoading = true,
            basketLoading = true,
            favouritesLoading = true,
            errorText = null
        ).also {
            pushResult { loadMenu() }
            pushResult { loadOrder(orderStorage.orderId ?: oldState.order?.id) }
            pushResult { loadBasket() }
            pushResult { loadFavourites() }
        }
        is Wish.AddToOrder -> oldState.also {
            sendMessage(resourceProvider.getString(R.string.requestSent))
            pushResult {
                sendSinglePortionOrderToServer(
                    wish.comment,
                    wish.drinksImmediately,
                    wish.portion,
                    oldState.order?.id,
                )
            }
        }
        is Wish.AddToBasket -> oldState.also { pushResult { addToBasket(wish.portion) } }
        is Wish.AddToFavourite -> oldState.also { pushResult { addToFavourites(wish.menuItemId) } }
        is Wish.RemoveFromBasket -> oldState.also { pushResult { removeFromBasket(wish.portion) } }
        is Wish.RemoveFromFavourite -> oldState.also { pushResult { removeFromFavourites(wish.menuItemId) } }
        is Wish.ChangeShowType -> oldState.copy(type = wish.type)
        Wish.SendBasketToServer -> {
            pushResult { clearBasket() }
            pushResult {
                sendBasketToServer(
                    oldState.comment,
                    oldState.drinksImmediately,
                    oldState.basket.orEmpty(),
                    oldState.order?.id,
                )
            }
            sendMessage(resourceProvider.getString(R.string.requestSent))
            oldState.copy(
                comment = "",
                drinksImmediately = true
            )
        }
        is Wish.ChangeComment -> oldState.copy(comment = wish.comment)
        is Wish.ShowDishDetails -> oldState.also {
            router.navigateTo(Screens.DishDetailsScreen(wish.dish, it.order?.id))
        }
        is Wish.SetFilter -> oldState.copy(filter = wish.filter)
            .recountMenuWithOrdersAndBasketAndFavouritesAndFilter()
        is Wish.DrinksImmediatelyChanged -> oldState.copy(drinksImmediately = wish.value)
        Wish.CloseOrder -> if (oldState.order?.status == "PAID") oldState.also {
            orderStorage.orderId = null
            router.newRootScreen(Screens.ScanScreen)
        } else oldState.also {
            pushResult { closeOrder(it.order?.id) }
        }
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        is Result.OrderResult.OrderNotCreated -> oldState.copy(
            orderLoading = false,
            subOrders = emptyList(),
            order = null,
        ).recountMenuWithOrdersAndBasketAndFavouritesAndFilter()
        is Result.OrderResult.OrderLoaded -> oldState.copy(
            orderLoading = false,
            subOrders = res.order?.subOrders.orEmpty(),
            order = res.order
        ).recountMenuWithOrdersAndBasketAndFavouritesAndFilter()
            .also { orderStorage.orderId = res.order?.id }
        is Result.OrderResult.Error -> {
            val tmpState = if (res.restoreDrinksFlag != null) oldState.copy(
                drinksImmediately = res.restoreDrinksFlag, comment = res.restoreComment.orEmpty()
            ) else oldState
            tmpState.copy(
                orderLoading = false,
                errorText = if (oldState.subOrders == null) res.text else oldState.errorText
            ).also {
                res.restoreBasket?.let { basket -> pushResult { restoreBasket(basket) } }
                sendMessage(res.text)
            }
        }
        is Result.MenuResult.Menu -> oldState.copy(menuLoading = false, menuItems = res.items)
            .recountMenuWithOrdersAndBasketAndFavouritesAndFilter()
        is Result.MenuResult.Error -> oldState.copy(
            menuLoading = false,
            errorText = if (oldState.menuItems == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
        is Result.BasketResult.Basket -> oldState.copy(basketLoading = false, basket = res.basket)
            .recountMenuWithOrdersAndBasketAndFavouritesAndFilter()
        is Result.BasketResult.Error -> oldState.copy(
            basketLoading = false,
            errorText = if (oldState.basket == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
        is Result.FavouriteResult.Favourites -> oldState.copy(
            favouritesLoading = false,
            favourites = res.items
        ).recountMenuWithOrdersAndBasketAndFavouritesAndFilter()
        is Result.FavouriteResult.Error -> oldState.copy(
            favouritesLoading = false,
            errorText = if (oldState.favourites == null) res.text else oldState.errorText
        ).also {
            sendMessage(res.text)
        }
        is Result.CloseOrder.Error -> oldState.also { sendMessage(res.text) }
        Result.CloseOrder.Success -> oldState.also {
            orderStorage.orderId = null
            router.newRootScreen(Screens.ScanScreen)
        }
    }

    private val startCookingItem by lazy {
        MenuButtonItem(resourceProvider.getString(R.string.startCooking), Wish.SendBasketToServer)
    }

    private fun State.recountMenuWithOrdersAndBasketAndFavouritesAndFilter(): State {
        if (subOrders == null || menuItems == null || basket == null || favourites == null) return copy(
            menuShowList = null,
            basketShowList = null,
            orderShowList = null,
            favouriteShowList = null
        )

        val menuTmp = menuItems.map { it.toDomain(subOrders, basket, favourites) }
        val basketTmp = menuTmp.filter { it.portions.any { p -> p.basketNumber > 0 } }
            .let { if (it.isEmpty()) emptyList() else it.plus(startCookingItem) }
        val orderTmp = subOrders.sortedByDescending { it.createdTimeStamp }
            .map { subOrder ->
                val portionIds: List<String> = subOrder.orderList.map { p -> p.portionId }
                val menuItemsFiltered = menuItems.filter {
                    it.portions.map { p -> p.id }.any { id -> id in portionIds }
                }
                val items =
                    menuItemsFiltered.map { it.toDomain(listOf(subOrder), basket, favourites) }
                listOf(
                    SubOrderTitle(
                        dateTime = subOrder.createdTimeStamp,
                        drinksImmediately = subOrder.drinksImmediately,
                        comment = subOrder.comment
                    )
                ) + items
            }.flatten()

        val menuFilteredToShow = when {
            filter == null -> menuTmp
            filter.tags.isEmpty() -> {
                if (filter.text.isEmpty()) menuTmp
                else menuTmp.filter { it.name.contains(filter.text, ignoreCase = true) }
            }
            else -> menuTmp.filter {
                (filter.text.isEmpty() || it.name.contains(filter.text, ignoreCase = true))
                        && it.tags.intersect(filter.tags).isNotEmpty()
            }
        }

        return copy(
            errorText = null,
            menuShowList = menuFilteredToShow,
            basketShowList = basketTmp,
            orderShowList = orderTmp,
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
        return addToBasket(portion.id)
    }

    private suspend fun removeFromBasket(portion: DomainPortion): Result.BasketResult {
        return when (val res = basketManager.removeFromBasket(restaurantId, portion.id)) {
            is ApiCallResult.Result -> Result.BasketResult.Basket(res.value)
            is ApiCallResult.ErrorOccurred -> Result.BasketResult.Error(res.text)
        }
    }

    private suspend fun restoreBasket(oldBasket: List<OrderInfo>): Result.BasketResult {
        val portionIds = oldBasket.map { info -> List(info.ordered) { info.portionId } }.flatten()
        return addToBasket(*portionIds.toTypedArray())
    }

    private suspend fun addToBasket(vararg portionIds: String): Result.BasketResult {
        return when (val res = basketManager.addToBasket(restaurantId, *portionIds)) {
            is ApiCallResult.Result -> Result.BasketResult.Basket(res.value)
            is ApiCallResult.ErrorOccurred -> Result.BasketResult.Error(res.text)
        }
    }

    private suspend fun clearBasket(): Result.BasketResult {
        return when (val res = basketManager.clearBasket()) {
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

    private suspend fun loadOrder(orderId: String?): Result.OrderResult {
        if (orderId == null) return Result.OrderResult.OrderNotCreated

        return when (val res = net.callResult { getOrderById(orderId) }) {
            is ApiCallResult.Result -> Result.OrderResult.OrderLoaded(res.value.order)
            is ApiCallResult.ErrorOccurred -> Result.OrderResult.Error(null, null, null, res.text)
        }
    }

    private suspend fun sendBasketToServer(
        comment: String,
        drinksImmediately: Boolean,
        basket: List<OrderInfo>,
        orderId: String?,
    ): Result.OrderResult {
        val error by lazy { Result.OrderResult.Error(basket, comment, drinksImmediately, "") }

        val request = AddSuborderRequest(
            comment = comment,
            drinksImmediately = drinksImmediately,
            portions = basket.map { dish -> List(dish.ordered) { dish.portionId } }.flatten()
        )

        if (orderId == null) return when (
            val res = net.callResult { createOrder(CreateOrderRequest(tableId, request)) }
        ) {
            is ApiCallResult.Result -> Result.OrderResult.OrderLoaded(res.value.order)
            is ApiCallResult.ErrorOccurred -> error.copy(text = res.text)
        }

        return when (val res = net.callResult { addToCurrentOrder(orderId, request) }) {
            is ApiCallResult.Result -> Result.OrderResult.OrderLoaded(res.value.order)
            is ApiCallResult.ErrorOccurred -> error.copy(text = res.text)
        }
    }

    private suspend fun sendSinglePortionOrderToServer(
        comment: String,
        drinksImmediately: Boolean,
        order: OrderInfo,
        orderId: String?,
    ): Result.OrderResult {
        val error by lazy { Result.OrderResult.Error(null, null, null, "") }

        val request = AddSuborderRequest(
            comment = comment,
            drinksImmediately = drinksImmediately,
            portions = List(order.ordered) { order.portionId }
        )

        if (orderId == null) return when (
            val res = net.callResult { createOrder(CreateOrderRequest(tableId, request)) }
        ) {
            is ApiCallResult.Result -> Result.OrderResult.OrderLoaded(res.value.order)
            is ApiCallResult.ErrorOccurred -> error.copy(text = res.text)
        }

        return when (val res = net.callResult { addToCurrentOrder(orderId, request) }) {
            is ApiCallResult.Result -> Result.OrderResult.OrderLoaded(res.value.order)
            is ApiCallResult.ErrorOccurred -> error.copy(text = res.text)
        }
    }

    private suspend fun closeOrder(orderId: String?): Result.CloseOrder {
        if (orderId == null) return Result.CloseOrder.Success
        return when (val res = net.callResult { closeOrder(orderId) }) {
            is ApiCallResult.Result -> Result.CloseOrder.Success
            is ApiCallResult.ErrorOccurred -> Result.CloseOrder.Error(res.text)
        }
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