package com.lacker.visitors.features.menu

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.utils.extensions.loadFromNet
import com.lacker.visitors.R
import kotlinx.android.synthetic.main.item_menu_item.*

fun getMenuAdapter(
    onAddToOrder: (DomainPortion) -> Unit,
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
    getDomainMenuItemAdapter(onAddToOrder, onItemClick)
)

fun getDomainMenuItemAdapter(
    onAddToOrder: (DomainPortion) -> Unit,
    onItemClick: (DomainMenuItem) -> Unit
) = adapterDelegateLayoutContainer<DomainMenuItem, DomainMenuItem>(R.layout.item_menu_item) {

    menuItemContainer.setOnClickListener { onItemClick(item) }

    bind {
        menuItemName.text = item.name
        menuItemDescription.text = item.shortDescription

        menuItemPicture.loadFromNet(url = item.photoFullUrl)
    }

}