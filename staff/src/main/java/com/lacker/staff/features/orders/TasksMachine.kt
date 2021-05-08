package com.lacker.staff.features.orders

import com.lacker.staff.R
import com.lacker.staff.data.api.ApiCallResult
import com.lacker.staff.data.api.NetworkManager
import com.lacker.staff.data.dto.calls.StaffCall
import com.lacker.staff.data.dto.orders.SubOrderListItem
import com.lacker.staff.data.storage.restaurants.RestaurantStorage
import com.lacker.staff.data.storage.user.User
import com.lacker.staff.data.storage.user.UserStorage
import javax.inject.Inject
import com.lacker.staff.features.orders.TasksMachine.Wish
import com.lacker.staff.features.orders.TasksMachine.State
import com.lacker.staff.features.orders.TasksMachine.Result
import com.lacker.staff.navigation.Screens
import com.lacker.utils.extensions.onNull
import com.lacker.utils.resources.ResourceProvider
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine
import voodoo.rocks.paginator.reduce.Ask
import voodoo.rocks.paginator.reduce.PaginationList
import voodoo.rocks.paginator.reduce.Receive
import voodoo.rocks.paginator.reduce.defaultErrorMessage

class TasksMachine @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val router: Router,
    private val net: NetworkManager,
    private val restaurantStorage: RestaurantStorage,
    private val userStorage: UserStorage,
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        data class ChangeShowType(val type: State.Type) : Wish()
        data class PaginationAsk(val type: State.Type, val ask: Ask) : Wish()
    }

    sealed class Result {
        data class ReceiveNewOrders(val receive: Receive<SubOrderListItem>) : Result()

        object NeedAuth : Result()
    }

    data class State(
        val newOrders: PaginationList<SubOrderListItem> = PaginationList.EmptyProgress(),
        val oldOrders: PaginationList<SubOrderListItem> = PaginationList.EmptyProgress(),
        val newCalls: PaginationList<StaffCall> = PaginationList.EmptyProgress(),
        val oldCalls: PaginationList<StaffCall> = PaginationList.EmptyProgress(),
        val newOrdersTotalCount: Int = 0,
        val newCallsTotalCount: Int = 0,
        val type: Type = Type.NEW_ORDERS
    ) {
        enum class Type {
            NEW_ORDERS, NEW_CALLS, OLD_ORDERS, OLD_CALLS
        }
    }

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        is Wish.ChangeShowType -> oldState.copy(type = wish.type)
        is Wish.PaginationAsk -> onPaginationAsk(oldState, wish.type, wish.ask)
    }

    private fun onPaginationAsk(oldState: State, type: State.Type, ask: Ask): State = when (type) {
        State.Type.NEW_ORDERS -> oldState.copy(newOrders = oldState.newOrders.onAsk(ask) {
            pushResult { getOrders(it, oldState.newOrders.lastOrNull()) }
        })
        State.Type.NEW_CALLS -> oldState.copy(newCalls = oldState.newCalls.onAsk(ask) {
            // TODO push result
        })
        State.Type.OLD_ORDERS -> oldState.copy(oldOrders = oldState.oldOrders.onAsk(ask) {
            // TODO push result
        })
        State.Type.OLD_CALLS -> oldState.copy(oldCalls = oldState.oldCalls.onAsk(ask) {
            // TODO push result
        })
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        Result.NeedAuth -> oldState.also { logOut() }
        is Result.ReceiveNewOrders -> oldState.copy(
            newOrders = oldState.newOrders.onReceive(res.receive) { error ->
                error.defaultErrorMessage()?.let { sendMessage(it) }
            }
        )
    }

    private suspend fun getOrders(pageNumber: Int, lastOrder: SubOrderListItem?): Result {
        val restaurantId = restaurantStorage.restaurant?.id ?: return Result.NeedAuth
        val lastOrderId = if (pageNumber <= 1) null else lastOrder?.id
        val res = net.callResult {
            getNewOrders(
                restaurantId = restaurantId,
                lastReceivedSuborderId = lastOrderId,
            )
        }
        val receive = when (res) {
            is ApiCallResult.Result -> Receive.NewPage(pageNumber, res.value.subOrders)
            is ApiCallResult.ErrorOccurred -> Receive.PageError(res.text)
        }
        return Result.ReceiveNewOrders(receive)
    }

    private fun logOut() {
        userStorage.user = User.empty()
        restaurantStorage.restaurant = null
        sendMessage(resourceProvider.getString(R.string.tokenError))
        router.newRootScreen(Screens.SignInScreen)
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