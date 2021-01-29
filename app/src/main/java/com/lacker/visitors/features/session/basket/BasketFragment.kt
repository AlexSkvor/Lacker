package com.lacker.visitors.features.session.basket

import com.lacker.visitors.R
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.session.SessionScreen
import com.lacker.visitors.features.session.basket.BasketMachine.Wish
import com.lacker.visitors.features.session.basket.BasketMachine.State

class BasketFragment : ToolbarFluxFragment<Wish, State>(), SessionScreen {

    companion object {
        fun newInstance() = BasketFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_basket

    override val machine by lazy { getMachineFromFactory(BasketMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.basketScreenTitle),
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