package com.lacker.visitors.features.session.menu

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lacker.utils.extensions.*
import com.lacker.visitors.R
import com.lacker.visitors.features.auth.bottomdialog.withAuthCheck
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.visitors.features.session.callstaff.openCallStaffDialog
import com.lacker.visitors.features.session.comment.CommentBeforeOrderBottomSheetDialogFragment
import com.lacker.visitors.features.session.comment.orderSingleItem
import com.lacker.visitors.features.session.common.DomainMenuItem
import com.lacker.visitors.features.session.common.DomainPortion
import com.lacker.visitors.features.session.common.MenuButtonItem
import com.lacker.visitors.features.session.common.getMenuAdapter
import com.lacker.visitors.features.session.menu.MenuMachine.Wish
import com.lacker.visitors.features.session.menu.MenuMachine.State
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
            menuResId = currentMenu // TODO filters as right-side NavigationDrawer
        )

    private val currentMenu: Int
        get() = if (machine.states().value.type != State.Type.MENU) R.menu.main_menu_menu
        else R.menu.main_menu_menu_with_filters

    private val currentTitle: String
        get() = when (machine.states().value.type) {
            State.Type.MENU -> getString(R.string.menuScreenTitle)
            State.Type.FAVOURITE -> getString(R.string.favouriteScreenTitle)
            State.Type.BASKET -> getString(R.string.basketScreenTitle)
            State.Type.ORDER -> getString(R.string.orderScreenTitle)
        }

    override val machine by lazy { getMachineFromFactory(MenuMachine::class.java) }

    override fun layoutRes(): Int = R.layout.fragment_menu

    private var commentFragment: CommentBeforeOrderBottomSheetDialogFragment? = null

    private fun createCommentFragment() {
        if (commentFragment == null)
            commentFragment = CommentBeforeOrderBottomSheetDialogFragment()
                .apply {
                    addToBasketListener = ::onAddPortionToBasket
                    removeFromBasketListener = ::onRemovePortionFromBasket
                    commentListener = { performWish(Wish.ChangeComment(it)) }
                    submitListener = { performWish(Wish.SendBasketToServer) }
                    onDismissListener = { commentFragment = null }
                    val state = machine.states().value
                    render(
                        state.basketShowList.orEmpty().filterIsInstance<DomainMenuItem>(),
                        state.comment
                    )
                }
    }

    private val menuAdapter by lazy { createAdapter() }
    private val favouriteAdapter by lazy { createAdapter() }
    private val basketAdapter by lazy { createAdapter() }
    private val orderAdapter by lazy { createAdapter() }

    private fun onMenuItemClick(item: DomainMenuItem) {
        performWish(Wish.ShowDishDetails(item))
    }

    private fun onFavouriteClick(item: DomainMenuItem) {
        if (item.inFavourites) performWish(Wish.RemoveFromFavourite(item.id))
        else performWish(Wish.AddToFavourite(item.id))
    }

    private fun onAddPortionToOrderClick(item: DomainMenuItem, portion: DomainPortion) {
        withAuthCheck(false, R.string.orderCreationAuthReason) {
            orderSingleItem(item, portion.id) { comment, info ->
                performWish(Wish.AddToOrder(comment, info))
            }
        }
    }

    private fun onRemovePortionFromBasket(portion: DomainPortion) {
        performWish(Wish.RemoveFromBasket(portion))
    }

    private fun onAddPortionToBasket(portion: DomainPortion) {
        performWish(Wish.AddToBasket(portion))
    }

    private fun onButtonClick(item: MenuButtonItem) {
        item.wish?.let {
            if (it is Wish.SendBasketToServer) {
                withAuthCheck(reasonRes = R.string.orderCreationAuthReason) {
                    createCommentFragment()
                    commentFragment?.show(childFragmentManager)
                }
            }
        }
    }

    private val recyclers: List<RecyclerView>
        get() = listOf(menuRecycler, favouriteRecycler, basketRecycler, orderRecycler)

    private val allViews
        get() = listOf(
            menuErrorPlaceholder,
            menuProgressPlaceholder,
            menuEmptyPlaceholder
        ) + recyclers

    override fun onScreenInit() {
        menuRecycler.adapter = menuAdapter
        favouriteRecycler.adapter = favouriteAdapter
        basketRecycler.adapter = basketAdapter
        orderRecycler.adapter = orderAdapter

        menuSwipeRefresh.setColorSchemeColors(colorCompat(R.color.blue))

        allViews.forEach {
            it.onScroll { upper ->
                if (upper) menuNavigationBar.appearFromBottom(200)
                else menuNavigationBar.hideBelowBottom(200)
            }
        }

        menuErrorPlaceholder.onRetry { performWish(Wish.Refresh) }
        menuSwipeRefresh.setOnRefreshListener { performWish(Wish.Refresh) }
        menuNavigationBar.onStateChange {
            performWish(Wish.ChangeShowType(it.asDomain()))
        }
        performWish(Wish.Refresh)
        recyclers.disableBlinking()
    }

    override fun onDestroyView() {
        recyclers.forEach { it.adapter = null }
        super.onDestroyView()
    }

    private var prevStateType: State.Type? = null

    private fun recyclerForStateType(type: State.Type): RecyclerView = when (type) {
        State.Type.MENU -> menuRecycler
        State.Type.FAVOURITE -> favouriteRecycler
        State.Type.BASKET -> basketRecycler
        State.Type.ORDER -> orderRecycler
    }

    private fun nextVisibleViewFromState(state: State): View = when {
        state.errorText.isNotNull() -> menuErrorPlaceholder
        (state.showLoading && state.empty) -> menuProgressPlaceholder
        state.empty && !state.showLoading && state.errorText.isNull() -> menuEmptyPlaceholder
        else -> recyclerForStateType(state.type)
    }

    private val currentVisibleView
        get() = allViews.firstOrNull { it.visible }

    private fun shouldAppearFromRight(newType: State.Type): Boolean {
        val oldPos = State.Type.values().indexOf(prevStateType)
        val newPos = State.Type.values().indexOf(newType)
        return oldPos < newPos
    }

    private fun renderVisibleView(state: State) {
        val nextVisibleView = nextVisibleViewFromState(state)
        val previousVisibleView = currentVisibleView
        allViews.forEach {
            if (it != nextVisibleView && it != previousVisibleView)
                it.gone()
        }
        val typeNotChanged = prevStateType == state.type || prevStateType == null
        if (typeNotChanged) {
            if (previousVisibleView != nextVisibleView)
                previousVisibleView?.gone()
            nextVisibleView.visible()
            nextVisibleView.x = 0f
        } else {
            if (shouldAppearFromRight(state.type)) {
                previousVisibleView?.hideOnLeft(300L)
                nextVisibleView.appearFromRight(300L)
            } else {
                previousVisibleView?.hideOnRight(300L)
                nextVisibleView.appearFromLeft(300L)
            }
        }
    }

    override fun render(state: State) {
        if (state.empty || state.errorText.isNotNull())
            menuNavigationBar.appearFromBottom(0)
        menuNavigationBar.setState(state.type.asUi())

        menuNavigationBar.setFavouriteBadge(state.favourites.orEmpty().size)
        menuNavigationBar.setBasketBadge(state.basket.orEmpty().sumBy { it.ordered })
        menuNavigationBar.setOrderBadge(
            state.subOrders.orEmpty()
                .map { it.orderList }
                .flatten()
                .sumBy { it.ordered }
        )

        refreshToolbar()

        menuErrorPlaceholder.errorText = state.errorText

        menuProgressPlaceholder.cookingThingText = currentTitle.toLowerCase(Locale.getDefault())

        menuSwipeRefresh.isEnabled = !state.empty
        menuSwipeRefresh.isRefreshing = state.showLoading

        menuEmptyPlaceholder.emptyThingText = currentTitle

        renderVisibleView(state)

        when (state.type) {
            State.Type.MENU -> menuAdapter.items = state.showList.orEmpty()
            State.Type.FAVOURITE -> favouriteAdapter.items = state.showList.orEmpty()
            State.Type.BASKET -> basketAdapter.items = state.showList.orEmpty()
            State.Type.ORDER -> orderAdapter.items = state.showList.orEmpty()
        }
        prevStateType = state.type

        commentFragment?.render(
            state.basketShowList.orEmpty().filterIsInstance<DomainMenuItem>(),
            state.comment
        )
    }

    override fun onMenuItemChosen(itemId: Int): Boolean {
        when (itemId) {
            R.id.bellIcon1 -> {
                openCallStaffDialog()
                return true
            }
            R.id.bellIcon2 -> {
                openCallStaffDialog()
                return true
            }
            R.id.filtersIcon -> {
                alsoPrintDebug("AAAAAAAA")
                return true
            }
        }

        return false
    }

    private fun createAdapter() = getMenuAdapter(
        onAddToOrder = ::onAddPortionToOrderClick,
        onItemClick = ::onMenuItemClick,
        removeFromBasket = ::onRemovePortionFromBasket,
        onAddToBasket = ::onAddPortionToBasket,
        onButtonClick = ::onButtonClick,
        onFavouriteClick = ::onFavouriteClick
    )
}