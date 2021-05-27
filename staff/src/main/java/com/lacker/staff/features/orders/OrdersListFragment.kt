package com.lacker.staff.features.orders

import com.lacker.staff.R
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.staff.features.orders.OrdersListMachine.Wish
import com.lacker.staff.features.orders.OrdersListMachine.State
import com.lacker.utils.extensions.colorCompat
import kotlinx.android.synthetic.main.fragment_only_pagination.*

class OrdersListFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = OrdersListFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_only_pagination

    override val machine by lazy { getMachineFromFactory(OrdersListMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.allOrdersListTitle),
            subtitle = null,
            showBackIcon = true,
            menuResId = null
        )
    }

    private val adapter by lazy {
        getOrderAdapter(
            onViewClick = { performWish(Wish.OpenOrder(it)) }
        )
    }

    override fun onScreenInit() {
        paginationFragmentContainer.apply {
            swipeRefresh?.setColorSchemeColors(colorCompat(R.color.blue))
            onAsk { performWish(Wish.PaginationAsk(it)) }
            addOrReplaceExistingAdapter(adapter)
            startRefresh()
        }
    }

    override fun render(state: State) {
        paginationFragmentContainer.setList(state.orders)
    }
}