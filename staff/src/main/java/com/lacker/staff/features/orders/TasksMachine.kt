package com.lacker.staff.features.orders

import com.lacker.dto.appeal.AppealDto
import com.lacker.staff.R
import com.lacker.staff.data.api.ApiCallResult
import com.lacker.staff.data.api.NetworkManager
import com.lacker.staff.data.dto.orders.SubOrderListItem
import com.lacker.staff.data.storage.menu.SubOrderSource
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
    private val userStorage: UserStorage,
    private val subOrdersSource: SubOrderSource,
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        data class ChangeShowType(val type: State.Type) : Wish()
        data class PaginationAsk(val type: State.Type, val ask: Ask) : Wish()

        data class ViewSuborder(val suborder: SubOrderListItem) : Wish()
        data class AcceptSuborder(val suborder: SubOrderListItem) : Wish()
        data class AcceptAppeal(val appeal: AppealDto) : Wish()
    }

    sealed class Result {
        data class ReceiveNewOrders(val receive: Receive<SubOrderListItem>) : Result()
        data class ReceiveOldOrders(val receive: Receive<SubOrderListItem>) : Result()
        data class ReceiveNewAppeals(val receive: Receive<AppealDto>) : Result()
        data class ReceiveOldAppeals(val receive: Receive<AppealDto>) : Result()

        sealed class AcceptSuborder : Result() {
            data class Success(val suborder: SubOrderListItem) : AcceptSuborder()
            object Error : AcceptSuborder()
        }

        sealed class AcceptAppeal : Result() {
            object Success : AcceptAppeal()
            object Error : AcceptAppeal()
        }

        object NeedAuth : Result()
    }

    data class State(
        val newOrders: PaginationList<SubOrderListItem> = PaginationList.EmptyProgress(),
        val oldOrders: PaginationList<SubOrderListItem> = PaginationList.EmptyProgress(),
        val newCalls: PaginationList<AppealDto> = PaginationList.EmptyProgress(),
        val oldCalls: PaginationList<AppealDto> = PaginationList.EmptyProgress(),
        val type: Type = Type.NEW_ORDERS,
    ) {
        val newOrdersTotalCount: Int = newOrders.size
        val newCallsTotalCount: Int = newCalls.size

        enum class Type {
            NEW_ORDERS, NEW_CALLS, OLD_ORDERS, OLD_CALLS
        }
    }

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        is Wish.ChangeShowType -> oldState.copy(type = wish.type)
        is Wish.PaginationAsk -> onPaginationAsk(oldState, wish.type, wish.ask)
        is Wish.AcceptSuborder -> oldState.also { pushResult { acceptSuborder(wish.suborder) } }
        is Wish.ViewSuborder -> oldState.also {
            router.navigateTo(Screens.SuborderScreen(wish.suborder))
        }
        is Wish.AcceptAppeal -> oldState.also { pushResult { acceptAppeal(wish.appeal) } }
    }

    private fun onPaginationAsk(oldState: State, type: State.Type, ask: Ask): State = when (type) {
        State.Type.NEW_ORDERS -> oldState.copy(newOrders = oldState.newOrders.onAsk(ask) {
            pushResult { getNewOrders(it) }
        })
        State.Type.NEW_CALLS -> oldState.copy(newCalls = oldState.newCalls.onAsk(ask) {
            pushResult { getNewAppeals(it) }
        })
        State.Type.OLD_ORDERS -> oldState.copy(oldOrders = oldState.oldOrders.onAsk(ask) {
            pushResult { getOldOrders(it) }
        })
        State.Type.OLD_CALLS -> oldState.copy(oldCalls = oldState.oldCalls.onAsk(ask) {
            pushResult { getOldAppeals(it) }
        })
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        Result.NeedAuth -> oldState.also { logOut() }
        is Result.ReceiveNewOrders -> oldState.copy(
            newOrders = oldState.newOrders.onReceive(res.receive) { error ->
                error.defaultErrorMessage()?.let { sendMessage(it) }
            }
        )
        is Result.ReceiveOldOrders -> oldState.copy(
            oldOrders = oldState.oldOrders.onReceive(res.receive) { error ->
                error.defaultErrorMessage()?.let { sendMessage(it) }
            }
        )
        is Result.AcceptSuborder.Success -> oldState.also {
            router.navigateTo(Screens.SuborderScreen(res.suborder))
        }
        Result.AcceptSuborder.Error -> oldState.copy(
            newOrders = oldState.newOrders.onAsk(Ask.Refresh) { pushResult { getNewOrders(it) } },
            oldOrders = oldState.oldOrders.onAsk(Ask.Refresh) { pushResult { getOldOrders(it) } }
        ).also { sendMessage(resourceProvider.getString(R.string.couldNotAcceptSuborder)) }
        Result.AcceptAppeal.Success -> oldState.copy(
            newCalls = oldState.newCalls.onAsk(Ask.Refresh) { pushResult { getNewAppeals(it) } },
            oldCalls = oldState.oldCalls.onAsk(Ask.Refresh) { pushResult { getOldAppeals(it) } }
        )
        Result.AcceptAppeal.Error -> oldState.copy(
            newCalls = oldState.newCalls.onAsk(Ask.Refresh) { pushResult { getNewAppeals(it) } },
            oldCalls = oldState.oldCalls.onAsk(Ask.Refresh) { pushResult { getOldAppeals(it) } }
        ).also { sendMessage(resourceProvider.getString(R.string.couldNotAcceptAppeal)) }
        is Result.ReceiveNewAppeals -> oldState.copy(
            newCalls = oldState.newCalls.onReceive(res.receive) { error ->
                error.defaultErrorMessage()?.let { sendMessage(it) }
            }
        )
        is Result.ReceiveOldAppeals -> oldState.copy(
            oldCalls = oldState.oldCalls.onReceive(res.receive) { error ->
                error.defaultErrorMessage()?.let { sendMessage(it) }
            }
        )
    }

    private suspend fun getNewOrders(pageNumber: Int): Result.ReceiveNewOrders {
        if (pageNumber > 1) return Result.ReceiveNewOrders(Receive.NewPage(pageNumber, emptyList()))

        val receive = when (val res = subOrdersSource.getSuborders(true)) {
            is ApiCallResult.Result -> Receive.NewPage(pageNumber, res.value)
            is ApiCallResult.ErrorOccurred -> Receive.PageError(res.text)
        }
        return Result.ReceiveNewOrders(receive)
    }

    private suspend fun getOldOrders(pageNumber: Int): Result.ReceiveOldOrders {
        if (pageNumber > 1) return Result.ReceiveOldOrders(Receive.NewPage(pageNumber, emptyList()))

        val receive = when (val res = subOrdersSource.getSuborders(false)) {
            is ApiCallResult.Result -> Receive.NewPage(pageNumber, res.value)
            is ApiCallResult.ErrorOccurred -> Receive.PageError(res.text)
        }
        return Result.ReceiveOldOrders(receive)
    }

    private suspend fun acceptSuborder(suborder: SubOrderListItem): Result.AcceptSuborder {
        return when (net.callResult { acceptSuborder(suborder.id) }) {
            is ApiCallResult.Result -> Result.AcceptSuborder.Success(suborder)
            is ApiCallResult.ErrorOccurred -> Result.AcceptSuborder.Error
        }
    }

    private suspend fun getNewAppeals(page: Int): Result.ReceiveNewAppeals {
        if (page > 1) return Result.ReceiveNewAppeals(Receive.NewPage(page, emptyList()))

        val r = when (val res = net.callResult { getNewAppeals(userStorage.user.restaurantId) }) {
            is ApiCallResult.Result -> Receive.NewPage(page, res.value.data)
            is ApiCallResult.ErrorOccurred -> Receive.PageError(res.text)
        }
        return Result.ReceiveNewAppeals(r)
    }

    private suspend fun getOldAppeals(page: Int): Result.ReceiveOldAppeals {
        if (page > 1) return Result.ReceiveOldAppeals(Receive.NewPage(page, emptyList()))

        val r = when (val res = net.callResult { getOldAppeals(userStorage.user.restaurantId) }) {
            is ApiCallResult.Result -> Receive.NewPage(page, res.value.data)
            is ApiCallResult.ErrorOccurred -> Receive.PageError(res.text)
        }
        return Result.ReceiveOldAppeals(r)
    }

    private suspend fun acceptAppeal(appeal: AppealDto): Result.AcceptAppeal {
        return when (net.callResult { acceptAppeal(appeal.id) }) {
            is ApiCallResult.Result -> Result.AcceptAppeal.Success
            is ApiCallResult.ErrorOccurred -> Result.AcceptAppeal.Error
        }
    }

    private fun logOut() {
        userStorage.user = User.empty()
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