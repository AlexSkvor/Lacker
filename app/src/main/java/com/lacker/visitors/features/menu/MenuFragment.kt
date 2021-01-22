package com.lacker.visitors.features.menu

import com.lacker.utils.extensions.alsoPrintDebug
import com.lacker.visitors.R
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.menu.MenuMachine.Wish
import com.lacker.visitors.features.menu.MenuMachine.State
import voodoo.rocks.flux.Machine

class MenuFragment : ToolbarFluxFragment<Wish, State>() {

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.menuScreenTitle),
            subtitle = null,
            showBackIcon = false,
            menuResId = null // TODO filters icon & filters as right-side NavigationDrawer
        )
    }
    override val machine: Machine<Wish, *, State>
        get() = TODO("Not yet implemented")

    override fun layoutRes(): Int {
        TODO("Not yet implemented")
    }

    private val adapter by lazy { getMenuAdapter(::onAddPortionToOrderClick, ::onMenuItemClick) }

    private fun onMenuItemClick(item: DomainMenuItem) {
        item.alsoPrintDebug("onMenuItemClick")
    }

    private fun onAddPortionToOrderClick(portion: DomainPortion) {
        portion.alsoPrintDebug("onAddPortionToOrderClick")
    }

    override fun onScreenInit() {
        TODO("Not yet implemented")
    }

    override fun render(state: State) {
        TODO("Not yet implemented")
    }
}