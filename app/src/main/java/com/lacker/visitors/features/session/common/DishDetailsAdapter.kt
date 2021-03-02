package com.lacker.visitors.features.session.common

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.utils.extensions.gone
import com.lacker.utils.extensions.loadDrawableRes
import com.lacker.utils.extensions.loadFromNet
import com.lacker.utils.extensions.visible
import com.lacker.visitors.R
import com.lacker.visitors.views.PortionView
import kotlinx.android.synthetic.main.item_dish_details.*

fun getOnlyDishDetailsAdapter(
    onAddToOrder: (DomainMenuItem, DomainPortion) -> Unit,
    onAddToBasket: (DomainPortion) -> Unit,
    removeFromBasket: (DomainPortion) -> Unit,
    onFavouriteClick: (DomainMenuItem) -> Unit
) = AsyncListDifferDelegationAdapter(
    menuDiffUtil,
    getDishDetailsAdapter(onAddToOrder, onAddToBasket, removeFromBasket, onFavouriteClick)
)

fun getDishDetailsAdapter(
    onAddToOrder: (DomainMenuItem, DomainPortion) -> Unit,
    onAddToBasket: (DomainPortion) -> Unit,
    removeFromBasket: (DomainPortion) -> Unit,
    onFavouriteClick: (DomainMenuItem) -> Unit
) = adapterDelegateLayoutContainer<DomainMenuItem, MenuAdapterItem>(R.layout.item_dish_details) {

    favouriteMarkerDishDetails.setOnClickListener { onFavouriteClick(item) }

    bind {
        val favouriteDrawable = if (item.inFavourites) R.drawable.ic_baseline_favorite_24
        else R.drawable.ic_baseline_favorite_border_24
        favouriteMarkerDishDetails.loadDrawableRes(favouriteDrawable)

        dishDetailsName.text = item.name
        dishDetailsDescription.text = item.shortDescription

        dishDetailsPicture.loadFromNet(url = item.photoFullUrl)

        item.portions.forEachIndexed { i, portion ->
            if (dishDetailsPortionsContainer.childCount <= i)
                dishDetailsPortionsContainer.addView(PortionView(context))

            val portionView = dishDetailsPortionsContainer.getChildAt(i) as? PortionView
            portionView?.apply {
                setupForPortion(
                    portion = portion,
                    onAddToOrder = { onAddToOrder(item, portion) },
                    onAddToBasket = onAddToBasket,
                    removeFromBasket = removeFromBasket
                )
                visible()
            }
        }

        for (i in item.portions.size until dishDetailsPortionsContainer.childCount)
            dishDetailsPortionsContainer.getChildAt(i).gone()
    }

}