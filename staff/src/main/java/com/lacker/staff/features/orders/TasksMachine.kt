package com.lacker.staff.features.orders

import com.lacker.staff.R
import com.lacker.staff.data.dto.calls.StaffCall
import com.lacker.staff.data.dto.orders.SubOrderListItem
import javax.inject.Inject
import com.lacker.staff.features.orders.TasksMachine.Wish
import com.lacker.staff.features.orders.TasksMachine.State
import com.lacker.staff.features.orders.TasksMachine.Result
import com.lacker.utils.extensions.onNull
import com.lacker.utils.resources.ResourceProvider
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine
import voodoo.rocks.paginator.reduce.PaginationList

class TasksMachine @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val router: Router
) : Machine<Wish, Result, State>() {

    sealed class Wish {

    }

    sealed class Result {

    }

    data class State(
        val newOrders: PaginationList<SubOrderListItem> = PaginationList.EmptyProgress(),
        val oldOrders: PaginationList<SubOrderListItem> = PaginationList.EmptyProgress(),
        val newCalls: PaginationList<StaffCall> = PaginationList.EmptyProgress(),
        val oldCalls: PaginationList<StaffCall> = PaginationList.EmptyProgress(),
        val type: Type = Type.NEW_ORDERS
    ) {
        enum class Type {
            NEW_ORDERS, NEW_CALLS, OLD_ORDERS, OLD_CALLS
        }
    }

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = oldState

    override fun onResult(res: Result, oldState: State): State = oldState

    private var lastClickTime: Long? = null
    override fun onBackPressed() {
        val now = System.currentTimeMillis()
        if (lastClickTime == null || now - lastClickTime.onNull(0) > 2000) {
            lastClickTime = now
            sendMessage(resourceProvider.getString(R.string.clickAgainToCloseApp))
        } else router.exit()
    }
}