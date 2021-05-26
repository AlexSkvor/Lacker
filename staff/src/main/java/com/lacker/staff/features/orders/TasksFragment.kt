package com.lacker.staff.features.orders

import androidx.recyclerview.widget.RecyclerView
import com.lacker.staff.R
import com.lacker.staff.data.dto.orders.SubOrderListItem
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.staff.features.orders.TasksMachine.Wish
import com.lacker.staff.features.orders.TasksMachine.State
import com.lacker.staff.features.orders.adapters.getNewOrdersAdaptersList
import com.lacker.staff.utils.addOrReplaceExistingAdapters
import com.lacker.staff.views.asDomain
import com.lacker.staff.views.asUi
import com.lacker.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_tasks.*
import voodoo.rocks.paginator.reduce.Ask

class TasksFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = TasksFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_tasks

    override val machine by lazy { getMachineFromFactory(TasksMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings
        get() = ToolbarFragmentSettings(
            title = currentTitle,
            subtitle = null,
            showBackIcon = false,
            menuResId = null
        )

    private val currentTitle: String
        get() = when (machine.states().value.type) {
            State.Type.NEW_ORDERS -> getString(R.string.newOrdersScreenTitle)
            State.Type.NEW_CALLS -> getString(R.string.newCallsScreenTitle)
            State.Type.OLD_ORDERS -> getString(R.string.oldOrdersScreenTitle)
            State.Type.OLD_CALLS -> getString(R.string.oldCallsScreenTitle)
        }

    private val allViews
        get() = listOf(
            newOrdersPaginationview,
            newCallsPaginationview,
            oldOrdersPaginationview,
            oldCallsPaginationview
        )

    private val newOrdersAdapter by lazy {
        getNewOrdersAdaptersList(
            onViewClick = { onViewSuborderClicked(it) },
            onAcceptClick = { onAcceptSuborderClicked(it) },
            onRefresh = { performWish(Wish.PaginationAsk(State.Type.NEW_ORDERS, Ask.Refresh)) },
        )
    }

    private fun onViewSuborderClicked(subOrder: SubOrderListItem) {
        subOrder.alsoPrintDebug("View suborder not implemented!")
    }

    private fun onAcceptSuborderClicked(subOrder: SubOrderListItem) {
        subOrder.alsoPrintDebug("Accept suborder not implemented!")
    }

    override fun onScreenInit() {
        setupPaginationViews()
        setupNavigationView()
        tasksNavigationBar.onStateChange {
            performWish(Wish.ChangeShowType(it.asDomain()))
        }
    }

    private fun setupPaginationViews() {
        newOrdersPaginationview.apply {
            swipeRefresh?.setColorSchemeColors(colorCompat(R.color.blue))
            onAsk { performWish(Wish.PaginationAsk(State.Type.NEW_ORDERS, it)) }
            addOrReplaceExistingAdapters(newOrdersAdapter)
            startRefresh()
        }
        newCallsPaginationview.apply {
            swipeRefresh?.setColorSchemeColors(colorCompat(R.color.blue))
            onAsk { performWish(Wish.PaginationAsk(State.Type.NEW_CALLS, it)) }
            addOrReplaceExistingAdapters(newOrdersAdapter) // TODO different adapter
            startRefresh()
        }
        oldOrdersPaginationview.apply {
            swipeRefresh?.setColorSchemeColors(colorCompat(R.color.blue))
            onAsk { performWish(Wish.PaginationAsk(State.Type.OLD_ORDERS, it)) }
            addOrReplaceExistingAdapters(newOrdersAdapter) // TODO different adapter
            startRefresh()
        }
        oldCallsPaginationview.apply {
            swipeRefresh?.setColorSchemeColors(colorCompat(R.color.blue))
            onAsk { performWish(Wish.PaginationAsk(State.Type.OLD_CALLS, it)) }
            addOrReplaceExistingAdapters(newOrdersAdapter) // TODO different adapter
            startRefresh()
        }
    }

    private fun setupNavigationView() {
        allViews.forEach {
            it.findViewById<RecyclerView>(R.id.recyclerView).onScroll { upper ->
                if (upper) tasksNavigationBar.appearFromBottom(200)
                else tasksNavigationBar.hideBelowBottom(200)
            }
        }
    }

    override fun render(state: State) {
        refreshToolbar()
        renderTasksNavigationBar(
            type = state.type,
            ordersNumber = state.newOrdersTotalCount,
            callsNumber = state.newCallsTotalCount
        )
        renderVisibleView(state.type)
        renderList(state)
    }

    private fun renderTasksNavigationBar(type: State.Type, ordersNumber: Int, callsNumber: Int) {
        tasksNavigationBar.setState(type.asUi())
        tasksNavigationBar.setNewOrdersBadge(ordersNumber)
        tasksNavigationBar.setNewCallsBadge(callsNumber)
    }

    private fun renderList(state: State) {
        when (state.type) {
            State.Type.NEW_ORDERS -> newOrdersPaginationview.setList(state.newOrders)
            State.Type.NEW_CALLS -> newCallsPaginationview.setList(state.newCalls)
            State.Type.OLD_ORDERS -> oldOrdersPaginationview.setList(state.oldOrders)
            State.Type.OLD_CALLS -> oldCallsPaginationview.setList(state.oldCalls)
        }
    }

    private var prevStateType: State.Type? = null
    private fun renderVisibleView(nextStateType: State.Type) {

        fun shouldAppearFromRight(newType: State.Type): Boolean {
            val oldPos = State.Type.values().indexOf(prevStateType)
            val newPos = State.Type.values().indexOf(newType)
            return oldPos < newPos
        }

        val nextVisibleView = when (nextStateType) {
            State.Type.NEW_ORDERS -> newOrdersPaginationview
            State.Type.NEW_CALLS -> newCallsPaginationview
            State.Type.OLD_ORDERS -> oldOrdersPaginationview
            State.Type.OLD_CALLS -> oldCallsPaginationview
        }
        val previousVisibleView = allViews.firstOrNull { it.visible }

        allViews.forEach {
            if (it != previousVisibleView && it != nextVisibleView)
                it.gone()
        }
        val typeNotChanged = prevStateType == nextStateType || prevStateType == null
        if (typeNotChanged) {
            if (previousVisibleView != nextVisibleView)
                previousVisibleView?.gone()
            nextVisibleView.visible()
            nextVisibleView.x = 0f
        } else {
            if (shouldAppearFromRight(nextStateType)) {
                previousVisibleView?.hideOnLeft(300L)
                nextVisibleView.appearFromRight(300L)
            } else {
                previousVisibleView?.hideOnRight(300L)
                nextVisibleView.appearFromLeft(300L)
            }
        }
        prevStateType = nextStateType
    }

}