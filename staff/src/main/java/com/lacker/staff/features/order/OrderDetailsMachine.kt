package com.lacker.staff.features.order

import com.lacker.dto.order.OrderWithoutSuborders
import com.lacker.staff.data.api.ApiCallResult
import com.lacker.staff.data.api.NetworkManager
import com.lacker.staff.data.storage.menu.SubOrderSource
import javax.inject.Inject
import com.lacker.staff.features.order.OrderDetailsMachine.Wish
import com.lacker.staff.features.order.OrderDetailsMachine.State
import com.lacker.staff.features.order.OrderDetailsMachine.Result
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine
import voodoo.rocks.paginator.reduce.Ask
import voodoo.rocks.paginator.reduce.PaginationList
import voodoo.rocks.paginator.reduce.Receive
import voodoo.rocks.paginator.reduce.defaultErrorMessage

class OrderDetailsMachine @Inject constructor(
    private val router: Router,
    private val net: NetworkManager,
    private val subOrderSource: SubOrderSource,
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        data class SetOrderId(val id: String) : Wish()
        data class PaginationAsk(val ask: Ask) : Wish()

        object Close : Wish()
    }

    sealed class Result {
        data class OrderLoaded(val receive: Receive<Any>) : Result()

        sealed class CloseResult : Result() {
            object Success : CloseResult()
            data class Error(val text: String) : CloseResult()
        }
    }

    data class State(
        val orderId: String? = null,
        val items: PaginationList<Any> = PaginationList.EmptyProgress(),
    )

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        Wish.Close -> oldState.also { pushResult { closeOrder(requireNotNull(it.orderId)) } }
        is Wish.PaginationAsk -> oldState.copy(items = oldState.items.onAsk(wish.ask) {
            pushResult { loadOrder(it, requireNotNull(oldState.orderId)) }
        })
        is Wish.SetOrderId -> oldState.copy(orderId = wish.id)
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        is Result.CloseResult.Error -> oldState.also { sendMessage(res.text) }
        Result.CloseResult.Success -> oldState.copy(items = oldState.items.onAsk(Ask.Refresh) {
            pushResult { loadOrder(it, requireNotNull(oldState.orderId)) }
        })
        is Result.OrderLoaded -> oldState.copy(items = oldState.items.onReceive(res.receive) {
            it.defaultErrorMessage()?.let { error -> sendMessage(error) }
        })
    }

    private suspend fun loadOrder(page: Int, orderId: String): Result.OrderLoaded {
        if (page > 1) return Result.OrderLoaded(Receive.NewPage(page, emptyList()))

        val order = when (val res = net.callResult { getOrderById(orderId) }) {
            is ApiCallResult.Result -> res.value.data
            is ApiCallResult.ErrorOccurred -> return Result.OrderLoaded(Receive.PageError(res.text))
        }

        val orderWithoutSuborders = OrderWithoutSuborders(
            status = order.status,
            user = order.user,
            table = order.table,
            id = order.id,
            created = order.created
        )

        val suborders = when (val res = subOrderSource.mapSuborders(order.subOrders)) {
            is ApiCallResult.Result -> res.value.sortedByDescending { it.createdDateTime }
            is ApiCallResult.ErrorOccurred -> return Result.OrderLoaded(Receive.PageError(res.text))
        }

        val items = listOf(orderWithoutSuborders) +
                suborders.map { listOf(it) + it.orderList }.flatten()
        return Result.OrderLoaded(Receive.NewPage(page, items))
    }

    private suspend fun closeOrder(orderId: String): Result.CloseResult {
        return when (val res = net.callResult { closeOrder(orderId) }) {
            is ApiCallResult.Result -> Result.CloseResult.Success
            is ApiCallResult.ErrorOccurred -> Result.CloseResult.Error(res.text)
        }
    }

    override fun onBackPressed() {
        router.exit()
    }
}