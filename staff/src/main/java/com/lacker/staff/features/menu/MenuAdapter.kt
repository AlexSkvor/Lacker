package com.lacker.staff.features.menu

import com.google.android.material.chip.Chip
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.staff.R
import com.lacker.staff.data.dto.orders.Dish
import com.lacker.staff.views.PortionView
import com.lacker.utils.extensions.gone
import com.lacker.utils.extensions.loadFromNet
import com.lacker.utils.extensions.visible
import kotlinx.android.synthetic.main.item_dish.*

// TODO show stopped!
fun dishAdapter(
    showNumber: Boolean,
    showEmptyPortions: Boolean,
) = adapterDelegateLayoutContainer<Dish, Any>(R.layout.item_dish) {

    bind {

        menuItemPicture.loadFromNet(url = item.photoFullUrl)
        menuItemName.text = item.dishName
        menuItemDescription.text = item.shortDescription

        val portionsToShow = if (showEmptyPortions) item.portions
        else item.portions.filter { it.count >= 1 }

        portionsToShow.forEachIndexed { i, portion ->
            if (portionsContainer.childCount <= i)
                portionsContainer.addView(PortionView(context))

            val portionView = portionsContainer.getChildAt(i) as? PortionView
            portionView?.apply {
                setupForPortion(
                    showNumber = showNumber,
                    portion = portion,
                )
                visible()
            }
        }

        for (i in portionsToShow.size until portionsContainer.childCount)
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