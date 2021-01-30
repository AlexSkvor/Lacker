package com.lacker.visitors.features.session.menu

import androidx.recyclerview.widget.SimpleItemAnimator
import com.lacker.utils.extensions.alsoPrintDebug
import com.lacker.utils.extensions.getImplementation
import com.lacker.utils.extensions.visible
import com.lacker.visitors.R
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.session.SessionHolder
import com.lacker.visitors.features.session.SessionScreen
import com.lacker.visitors.features.session.menu.MenuMachine.Wish
import com.lacker.visitors.features.session.menu.MenuMachine.State
import com.lacker.visitors.utils.onScroll
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : ToolbarFluxFragment<Wish, State>(), SessionScreen {

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

    private val adapter by lazy {
        getMenuAdapter(
            onAddToOrder = ::onAddPortionToOrderClick,
            onItemClick = ::onMenuItemClick,
            removeFromBasket = ::onRemovePortionFromBasket,
            onAddToBasket = ::onAddPortionToBasket
        )
    }

    private val sessionHolder by lazy { getImplementation(SessionHolder::class.java) }

    private fun onMenuItemClick(item: DomainMenuItem) {
        item.alsoPrintDebug("onMenuItemClick")
    }

    private fun onAddPortionToOrderClick(portion: DomainPortion) {
        portion.alsoPrintDebug("onAddPortionToOrderClick")
    }

    private fun onRemovePortionFromBasket(portion: DomainPortion) {
        performWish(Wish.RemoveFromBasket(portion))
    }

    private fun onAddPortionToBasket(portion: DomainPortion) {
        performWish(Wish.AddToBasket(portion))
    }

    override fun onResume() {
        super.onResume()
        sessionHolder?.onSessionScreenStart(this)
    }

    override fun onScreenInit() {
        menuRecycler.adapter = adapter
        menuRecycler.onScroll {
            if (it) sessionHolder?.showNavigationAnimated()
            else sessionHolder?.closeNavigationAnimated()
        }
        menuErrorPlaceholder.onRetry { performWish(Wish.Refresh) }
        menuSwipeRefresh.setOnRefreshListener { performWish(Wish.Refresh) }
        performWish(Wish.Refresh)
        (menuRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    override fun onDestroyView() {
        menuRecycler.adapter = null
        super.onDestroyView()
    }

    override fun render(state: State) {
        adapter.items = state.menuWithOrders ?: emptyList()
        menuErrorPlaceholder.errorText = state.errorText
        menuProgressPlaceholder.visible = (state.showLoading && state.empty)
        menuSwipeRefresh.visible = !state.empty
        menuSwipeRefresh.isRefreshing = state.showLoading
    }
}