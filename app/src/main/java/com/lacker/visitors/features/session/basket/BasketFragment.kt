package com.lacker.visitors.features.session.basket

import androidx.recyclerview.widget.SimpleItemAnimator
import com.lacker.utils.extensions.alsoPrintDebug
import com.lacker.utils.extensions.visible
import com.lacker.visitors.R
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.session.basket.BasketMachine.Wish
import com.lacker.visitors.features.session.basket.BasketMachine.State
import com.lacker.visitors.features.session.common.DomainMenuItem
import com.lacker.visitors.features.session.common.DomainPortion
import com.lacker.visitors.features.session.common.MenuButtonItem
import com.lacker.visitors.features.session.common.getMenuAdapter
import kotlinx.android.synthetic.main.fragment_basket.*

class BasketFragment : ToolbarFluxFragment<Wish, State>() {

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

    private val adapter by lazy {
        getMenuAdapter(
            onAddToOrder = ::onAddPortionToOrderClick,
            onItemClick = ::onMenuItemClick,
            removeFromBasket = ::onRemovePortionFromBasket,
            onAddToBasket = ::onAddPortionToBasket,
            onButtonClick = ::onButtonClick
        )
    }

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

    private fun onButtonClick(item: MenuButtonItem) {
        item.wish?.let { if (it is Wish) performWish(it) }
    }

    override fun onScreenInit() {
        basketRecycler.adapter = adapter
        (basketRecycler.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        basketErrorPlaceholder.onRetry { performWish(Wish.Refresh) }
        basketSwipeRefresh.setOnRefreshListener { performWish(Wish.Refresh) }
        performWish(Wish.Refresh)
    }

    override fun render(state: State) {
        adapter.items = state.menuWithBasket ?: emptyList()
        basketErrorPlaceholder.errorText = state.errorText
        basketProgressPlaceholder.visible = (state.showLoading && state.empty)
        basketSwipeRefresh.visible = !state.empty
        basketSwipeRefresh.isRefreshing = state.showLoading
    }

    override fun onDestroyView() {
        basketRecycler.adapter = null
        super.onDestroyView()
    }
}