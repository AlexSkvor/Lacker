package com.lacker.visitors.features.session.dishdetails

import com.lacker.utils.extensions.getArgument
import com.lacker.utils.extensions.visible
import com.lacker.utils.extensions.withArguments
import com.lacker.visitors.R
import com.lacker.visitors.features.auth.bottomdialog.withAuthCheck
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.session.comment.orderSingleItem
import com.lacker.visitors.features.session.common.DomainMenuItem
import com.lacker.visitors.features.session.common.DomainPortion
import com.lacker.visitors.features.session.common.getOnlyDishDetailsAdapter
import com.lacker.visitors.features.session.dishdetails.DishDetailsMachine.Wish
import com.lacker.visitors.features.session.dishdetails.DishDetailsMachine.State
import com.lacker.visitors.utils.disableBlinking
import kotlinx.android.synthetic.main.fragment_dish_details.*

class DishDetailsFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance(dish: DomainMenuItem) = DishDetailsFragment()
            .withArguments(DISH_KEY to dish)

        private const val DISH_KEY = "DishDetailsFragment DISH_KEY"
    }

    override fun layoutRes(): Int = R.layout.fragment_dish_details

    override val machine by lazy { getMachineFromFactory(DishDetailsMachine::class.java) }

    private val dish: DomainMenuItem by lazy { getArgument(DISH_KEY) }

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
            orderSingleItem(item, portion.id) { comment, info ->
                performWish(Wish.AddToOrder(comment, info))
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
        performWish(Wish.SetDish(dish))
    }

    override fun render(state: State) {
        adapter.items = listOfNotNull(state.dish)
        dishDetailsProgress.visible = state.loading
    }
}