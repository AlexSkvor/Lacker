package com.lacker.visitors.features.session.menu

import androidx.recyclerview.widget.SimpleItemAnimator
import com.lacker.utils.extensions.*
import com.lacker.visitors.R
import com.lacker.visitors.features.auth.bottomdialog.withAuthCheck
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.session.callstaff.openCallStaffDialog
import com.lacker.visitors.features.session.common.DomainMenuItem
import com.lacker.visitors.features.session.common.DomainPortion
import com.lacker.visitors.features.session.common.MenuButtonItem
import com.lacker.visitors.features.session.common.getMenuAdapter
import com.lacker.visitors.features.session.menu.MenuMachine.Wish
import com.lacker.visitors.features.session.menu.MenuMachine.State
import com.lacker.visitors.utils.onScroll
import com.lacker.visitors.views.asDomain
import com.lacker.visitors.views.asUi
import kotlinx.android.synthetic.main.fragment_menu.*
import java.util.*

class MenuFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = MenuFragment()
    }

    override val toolbarSettings: ToolbarFragmentSettings
        get() = ToolbarFragmentSettings(
            title = currentTitle,
            subtitle = null,
            showBackIcon = false,
            menuResId = R.menu.main_menu_menu // TODO filters icon & filters as right-side NavigationDrawer
        )

    private val currentTitle: String
        get() = when (machine.states().value.type) {
            State.Type.MENU -> getString(R.string.menuScreenTitle)
            State.Type.FAVOURITE -> getString(R.string.favouriteScreenTitle)
            State.Type.BASKET -> getString(R.string.basketScreenTitle)
            State.Type.ORDER -> getString(R.string.orderScreenTitle)
        }

    override val machine by lazy { getMachineFromFactory(MenuMachine::class.java) }

    override fun layoutRes(): Int = R.layout.fragment_menu

    private val adapter by lazy {
        getMenuAdapter(
            onAddToOrder = ::onAddPortionToOrderClick,
            onItemClick = ::onMenuItemClick,
            removeFromBasket = ::onRemovePortionFromBasket,
            onAddToBasket = ::onAddPortionToBasket,
            onButtonClick = ::onButtonClick,
            onFavouriteClick = ::onFavouriteClick
        )
    }

    private fun onMenuItemClick(item: DomainMenuItem) {
        item.alsoPrintDebug("onMenuItemClick")
    }

    private fun onFavouriteClick(item: DomainMenuItem) {
        if (item.inFavourites) performWish(Wish.RemoveFromFavourite(item.id))
        else performWish(Wish.AddToFavourite(item.id))
    }

    private fun onAddPortionToOrderClick(portion: DomainPortion) {
        withAuthCheck(false, R.string.orderCreationAuthReason) {
            portion.alsoPrintDebug("onAddPortionToOrderClick")
        }
    }

    private fun onRemovePortionFromBasket(portion: DomainPortion) {
        performWish(Wish.RemoveFromBasket(portion))
    }

    private fun onAddPortionToBasket(portion: DomainPortion) {
        performWish(Wish.AddToBasket(portion))
    }

    private fun onButtonClick(item: MenuButtonItem) {
        item.wish?.let { if (it is Wish) performWish(it) }
    }

    override fun onScreenInit() {
        menuRecycler.adapter = adapter
        menuRecycler.onScroll {
            if (it) menuNavigationBar.appearFromBottom(200)
            else menuNavigationBar.hideBelowBottom(200)
        }
        menuErrorPlaceholder.onRetry { performWish(Wish.Refresh) }
        menuSwipeRefresh.setOnRefreshListener { performWish(Wish.Refresh) }
        menuNavigationBar.onStateChange {
            performWish(Wish.ChangeShowType(it.asDomain()))
        }
        performWish(Wish.Refresh)
        (menuRecycler.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
    }

    override fun onDestroyView() {
        menuRecycler.adapter = null
        super.onDestroyView()
    }

    override fun render(state: State) { // TODO save position for every state type
        menuErrorPlaceholder.errorText = state.errorText

        menuProgressPlaceholder.visible = (state.showLoading && state.empty)
        menuProgressPlaceholder.cookingThingText = currentTitle.toLowerCase(Locale.getDefault())

        menuSwipeRefresh.visible = !state.empty
        menuSwipeRefresh.isRefreshing = state.showLoading

        refreshToolbar()

        if (state.empty || state.errorText.isNotNull())
            menuNavigationBar.appearFromBottom(0)
        menuNavigationBar.setState(state.type.asUi())

        menuEmptyPlaceholder.emptyThingText = currentTitle.toLowerCase(Locale.getDefault())
        menuEmptyPlaceholder.visible = state.empty &&
                !(state.showLoading || state.errorText.isNotNull())

        adapter.items = state.showList.orEmpty()
    }

    override fun onMenuItemChosen(itemId: Int): Boolean {
        if (itemId == R.id.bellIcon1) {
            openCallStaffDialog()
            return true
        }

        return false
    }
}