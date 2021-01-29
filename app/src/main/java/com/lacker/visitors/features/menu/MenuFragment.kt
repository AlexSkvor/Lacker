package com.lacker.visitors.features.menu

import com.lacker.utils.extensions.alsoPrintDebug
import com.lacker.utils.extensions.visible
import com.lacker.visitors.R
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.menu.MenuMachine.Wish
import com.lacker.visitors.features.menu.MenuMachine.State
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = MenuFragment()
    }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.menuScreenTitle),
            subtitle = null,
            showBackIcon = false,
            menuResId = null // TODO filters icon & filters as right-side NavigationDrawer
        )
    }

    override val machine by lazy { getMachineFromFactory(MenuMachine::class.java) }

    override fun layoutRes(): Int = R.layout.fragment_menu

    private val adapter by lazy { getMenuAdapter(::onAddPortionToOrderClick, ::onMenuItemClick) }

    private fun onMenuItemClick(item: DomainMenuItem) {
        item.alsoPrintDebug("onMenuItemClick")
    }

    private fun onAddPortionToOrderClick(portion: DomainPortion) {
        portion.alsoPrintDebug("onAddPortionToOrderClick")
    }

    override fun onScreenInit() {
        menuRecycler.adapter = adapter
        menuErrorPlaceholder.onRetry { performWish(Wish.Refresh) }
        menuSwipeRefresh.setOnRefreshListener { performWish(Wish.Refresh) }
        performWish(Wish.Refresh)
    }

    override fun render(state: State) {
        adapter.items = state.menuWithOrders ?: emptyList()
        menuErrorPlaceholder.errorText = state.errorText
        menuProgressPlaceholder.visible = (state.showLoading && state.empty)
        menuSwipeRefresh.visible = !state.empty
        menuSwipeRefresh.isRefreshing = state.showLoading
    }
}