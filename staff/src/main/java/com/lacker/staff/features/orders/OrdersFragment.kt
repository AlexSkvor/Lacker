package com.lacker.staff.features.orders

import com.lacker.staff.R
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.staff.features.orders.OrdersMachine.Wish
import com.lacker.staff.features.orders.OrdersMachine.State

class OrdersFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = OrdersFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_orders

    override val machine by lazy { getMachineFromFactory(OrdersMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.ordersScreenTitle),
            subtitle = null,
            showBackIcon = false,
            menuResId = null
        )
    }

    override fun onScreenInit() {
        //TODO("Not yet implemented")
    }

    override fun render(state: State) {
        //TODO("Not yet implemented")
    }
}