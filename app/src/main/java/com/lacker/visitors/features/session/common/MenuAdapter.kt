package com.lacker.visitors.features.session.common

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

fun getMenuAdapter(
    onAddToOrder: (DomainPortion) -> Unit,
    onAddToBasket: (DomainPortion) -> Unit,
    removeFromBasket: (DomainPortion) -> Unit,
    onItemClick: (DomainMenuItem) -> Unit,
    onButtonClick: (Any?) -> Unit
) = AsyncListDifferDelegationAdapter(
    object : DiffUtil.ItemCallback<MenuAdapterItem>() {
        override fun areItemsTheSame(oldItem: MenuAdapterItem, newItem: MenuAdapterItem): Boolean {
            return when {
                oldItem is DomainMenuItem && newItem is DomainMenuItem -> oldItem.id == newItem.id
                oldItem is MenuButtonItem && newItem is MenuButtonItem -> oldItem == newItem
                else -> false
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: MenuAdapterItem,
            newItem: MenuAdapterItem
        ): Boolean {
            return when {
                oldItem is DomainMenuItem && newItem is DomainMenuItem -> oldItem == newItem
                oldItem is MenuButtonItem && newItem is MenuButtonItem -> oldItem == newItem
                else -> false
            }
        }
    },
    getDomainMenuItemAdapter(onAddToOrder, onAddToBasket, removeFromBasket, onItemClick),
    getMenuButtonItemAdapter(onButtonClick)
)