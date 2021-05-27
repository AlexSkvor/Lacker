package com.lacker.staff.features.orders

import com.lacker.dto.order.OrderWithoutSuborders
import com.lacker.staff.data.api.ApiCallResult
import com.lacker.staff.data.api.NetworkManager
import com.lacker.staff.data.storage.user.UserStorage
import javax.inject.Inject
import com.lacker.staff.features.orders.OrdersListMachine.Wish
import com.lacker.staff.features.orders.OrdersListMachine.State
import com.lacker.staff.features.orders.OrdersListMachine.Result
import com.lacker.staff.navigation.Screens
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine
import voodoo.rocks.paginator.reduce.Ask
import voodoo.rocks.paginator.reduce.PaginationList
import voodoo.rocks.paginator.reduce.Receive
import voodoo.rocks.paginator.reduce.defaultErrorMessage

class OrdersListMachine @Inject constructor(
    private val router: Router,
    private val net: NetworkManager,
    private val userStorage: UserStorage,
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        data class PaginationAsk(val ask: Ask) : Wish()
        data class OpenOrder(val order: OrderWithoutSuborders) : Wish()
    }

    sealed class Result {
        data class OrdersListReceive(val receive: Receive<OrderWithoutSuborders>) : Result()
    }

    data class State(
        val orders: PaginationList<OrderWithoutSuborders> = PaginationList.EmptyProgress(),
    )

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        is Wish.OpenOrder -> oldState.also { router.navigateTo(Screens.OrderScreen(wish.order.id)) }
        is Wish.PaginationAsk -> oldState.copy(orders = oldState.orders.onAsk(wish.ask) {
            pushResult { getOrders(it) }
        })
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        is Result.OrdersListReceive -> oldState.copy(orders = oldState.orders.onReceive(res.receive) {
            it.defaultErrorMessage()?.let { error -> sendMessage(error) }
        })
    }

    private suspend fun getOrders(page: Int): Result.OrdersListReceive {
        if (page > 1) return Result.OrdersListReceive(Receive.NewPage(page, emptyList()))

        val receive = when (val res = net.callResult { getOrders(userStorage.user.restaurantId) }) {
            is ApiCallResult.Result -> Receive.NewPage(page, res.value.data.sortedBy { it.status })
            is ApiCallResult.ErrorOccurred -> Receive.PageError(res.text)
        }
        return Result.OrdersListReceive(receive)
    }

    override fun onBackPressed() {
        router.exit()
    }
}