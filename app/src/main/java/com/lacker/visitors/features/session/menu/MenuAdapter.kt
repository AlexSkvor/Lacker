package com.lacker.visitors.features.session.menu

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.utils.extensions.gone
import com.lacker.utils.extensions.loadFromNet
import com.lacker.utils.extensions.visible
import com.lacker.visitors.R
import com.lacker.visitors.views.PortionView
import kotlinx.android.synthetic.main.item_menu_item.*

fun getMenuAdapter(
    onAddToOrder: (DomainPortion) -> Unit,
    onAddToBasket: (DomainPortion) -> Unit,
    removeFromBasket: (DomainPortion) -> Unit,
    onItemClick: (DomainMenuItem) -> Unit
) = AsyncListDifferDelegationAdapter(
    object : DiffUtil.ItemCallback<DomainMenuItem>() {
        override fun areItemsTheSame(oldItem: DomainMenuItem, newItem: DomainMenuItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DomainMenuItem, newItem: DomainMenuItem): Boolean {
            return oldItem == newItem
        }
    },
    getDomainMenuItemAdapter(onAddToOrder, onAddToBasket, removeFromBasket, onItemClick)
)

fun getDomainMenuItemAdapter(
    onAddToOrder: (DomainPortion) -> Unit,
    onAddToBasket: (DomainPortion) -> Unit,
    removeFromBasket: (DomainPortion) -> Unit,
    onItemClick: (DomainMenuItem) -> Unit
) = adapterDelegateLayoutContainer<DomainMenuItem, DomainMenuItem>(R.layout.item_menu_item) {

    menuItemContainer.setOnClickListener { onItemClick(item) }

    bind {
        menuItemName.text = item.name
        menuItemDescription.text = item.shortDescription

        menuItemPicture.loadFromNet(url = item.photoFullUrl)

        item.portions.forEachIndexed { i, portion ->
            if (portionsContainer.childCount <= i)
                portionsContainer.addView(PortionView(context))

            val portionView = portionsContainer.getChildAt(i) as? PortionView
            portionView?.apply {
                setupForPortion(portion, onAddToOrder, onAddToBasket, removeFromBasket)
                visible()
            }
        }

        for (i in item.portions.size until portionsContainer.childCount)
            portionsContainer.getChildAt(i).gone()
    }

}