package com.lacker.visitors.features.session.common

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.visitors.R
import kotlinx.android.synthetic.main.item_button.*

fun getMenuButtonItemAdapter(
    onItemClick: (MenuButtonItem) -> Unit
) = adapterDelegateLayoutContainer<MenuButtonItem, MenuAdapterItem>(R.layout.item_button) {

    itemButton.setOnClickListener { onItemClick(item) }

    bind {
        itemButton.text = item.text
    }

}