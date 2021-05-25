package com.lacker.visitors.features.session.common

import com.google.android.material.chip.Chip
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.utils.extensions.gone
import com.lacker.utils.extensions.loadDrawableRes
import com.lacker.utils.extensions.loadFromNet
import com.lacker.utils.extensions.visible
import com.lacker.visitors.R
import com.lacker.visitors.views.PortionView
import kotlinx.android.synthetic.main.item_menu_item.*

// TODO show blocked!
fun getDomainMenuItemAdapter(
    onAddToOrder: (DomainMenuItem, DomainPortion) -> Unit,
    onAddToBasket: (DomainPortion) -> Unit,
    removeFromBasket: (DomainPortion) -> Unit,
    onItemClick: (DomainMenuItem) -> Unit,
    onFavouriteClick: (DomainMenuItem) -> Unit
) = adapterDelegateLayoutContainer<DomainMenuItem, MenuAdapterItem>(R.layout.item_menu_item) {

    menuItemContainer.setOnClickListener { onItemClick(item) }
    favouriteMarkerMenuItem.setOnClickListener { onFavouriteClick(item) }

    bind {
        val favouriteDrawable = if (item.inFavourites) R.drawable.ic_baseline_favorite_24
        else R.drawable.ic_baseline_favorite_border_24
        favouriteMarkerMenuItem.loadDrawableRes(favouriteDrawable)

        menuItemName.text = item.name
        menuItemDescription.text = item.shortDescription

        menuItemPicture.loadFromNet(url = item.photoFullUrl)

        item.portions.forEachIndexed { i, portion ->
            if (portionsContainer.childCount <= i)
                portionsContainer.addView(PortionView(context))

            val portionView = portionsContainer.getChildAt(i) as? PortionView
            portionView?.apply {
                setupForPortion(
                    dishStopped = item.stopped,
                    portion = portion,
                    onAddToOrder = { onAddToOrder(item, portion) },
                    onAddToBasket = onAddToBasket,
                    removeFromBasket = removeFromBasket
                )
                visible()
            }
        }

        for (i in item.portions.size until portionsContainer.childCount)
            portionsContainer.getChildAt(i).gone()


        item.tags.forEachIndexed { i, tag ->
            if (menuItemTags.childCount <= i)
                menuItemTags.addView(Chip(context).apply {
                    setTextColor(getColor(R.color.white))
                    setChipBackgroundColorResource(R.color.blue)
                })

            val chipView = menuItemTags.getChildAt(i) as? Chip
            chipView?.apply {
                setText(tag.stringResource)
                visible()
            }
        }

        for (i in item.tags.size until menuItemTags.childCount)
            menuItemTags.getChildAt(i).gone()
    }

}