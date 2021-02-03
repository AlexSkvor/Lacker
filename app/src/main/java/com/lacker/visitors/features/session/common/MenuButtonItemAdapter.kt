package com.lacker.visitors.features.session.common

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.visitors.R
import kotlinx.android.synthetic.main.item_button.*

fun getMenuButtonItemAdapter(
    onItemClick: (Any?) -> Unit
) = adapterDelegateLayoutContainer<MenuButtonItem, MenuAdapterItem>(R.layout.item_button) {

    itemButton.setOnClickListener { onItemClick(item.wish) }

    bind {
        itemButton.text = item.text
    }

}