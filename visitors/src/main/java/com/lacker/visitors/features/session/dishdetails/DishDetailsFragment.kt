package com.lacker.visitors.features.session.dishdetails

import com.lacker.visitors.R
import com.lacker.visitors.features.auth.bottomdialog.withAuthCheck
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.utils.extensions.*
import com.lacker.visitors.features.session.comment.orderSingleItem
import com.lacker.visitors.features.session.common.DomainMenuItem
import com.lacker.visitors.features.session.common.DomainPortion
import com.lacker.visitors.features.session.common.getOnlyDishDetailsAdapter
import com.lacker.visitors.features.session.dishdetails.DishDetailsMachine.Wish
import com.lacker.visitors.features.session.dishdetails.DishDetailsMachine.State
import kotlinx.android.synthetic.main.fragment_dish_details.*

class DishDetailsFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance(dish: DomainMenuItem, orderId: String?) = DishDetailsFragment()
            .withArguments(
                DISH_KEY to dish,
                ORDER_ID_KEY to orderId.orEmpty(),
            )

        private const val DISH_KEY = "DishDetailsFragment DISH_KEY"
        private const val ORDER_ID_KEY = "DishDetailsFragment ORDER_ID_KEY"
    }

    override fun layoutRes(): Int = R.layout.fragment_dish_details

    override val machine by lazy { getMachineFromFactory(DishDetailsMachine::class.java) }

    private val dish: DomainMenuItem by lazy { getArgument(DISH_KEY) }
    private val orderId: String by lazy { getArgument(ORDER_ID_KEY) }

    private val adapter by lazy {
        getOnlyDishDetailsAdapter(
            onAddToOrder = ::onAddPortionToOrderClick,
            onAddToBasket = ::onAddPortionToBasket,
            removeFromBasket = ::onRemovePortionFromBasket,
            onFavouriteClick = ::onFavouriteClick
        )
    }

    private fun onAddPortionToOrderClick(item: DomainMenuItem, portion: DomainPortion) {
        withAuthCheck(false, R.string.orderCreationAuthReason) {
            orderSingleItem(item, portion.id) { comment, info, drinksAsap ->
                performWish(Wish.AddToOrder(comment, info, drinksAsap))
            }
        }
    }

    private fun onAddPortionToBasket(portion: DomainPortion) {
        performWish(Wish.AddToBasket(portion))
    }

    private fun onRemovePortionFromBasket(portion: DomainPortion) {
        performWish(Wish.RemoveFromBasket(portion))
    }

    private fun onFavouriteClick(item: DomainMenuItem) {
        if (item.inFavourites) performWish(Wish.RemoveFromFavourite)
        else performWish(Wish.AddToFavourite)
    }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = dish.name,
            subtitle = null,
            showBackIcon = true,
            menuResId = null
        )
    }

    override fun onScreenInit() {
        dishDetailsRecycler.adapter = adapter
        dishDetailsRecycler.disableBlinking()
        performWish(Wish.SetDishAndOrderId(dish, orderId))
    }

    override fun render(state: State) {
        adapter.items = listOfNotNull(state.dish)
        dishDetailsProgress.visible = state.loading
    }
}