package com.lacker.visitors.features.session.order

import com.lacker.visitors.R
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.session.SessionScreen
import com.lacker.visitors.features.session.order.OrderMachine.Wish
import com.lacker.visitors.features.session.order.OrderMachine.State

class OrderFragment : ToolbarFluxFragment<Wish, State>(), SessionScreen {

    companion object {
        fun newInstance() = OrderFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_order

    override val machine by lazy { getMachineFromFactory(OrderMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.orderScreenTitle),
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