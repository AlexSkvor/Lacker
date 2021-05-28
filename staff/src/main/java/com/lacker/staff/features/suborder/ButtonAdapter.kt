package com.lacker.staff.features.suborder

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.staff.R
import kotlinx.android.synthetic.main.item_button.*

data class Button(val text: String)

fun getButtonAdapter(
    onClick: () -> Unit,
) = adapterDelegateLayoutContainer<Button, Any>(R.layout.item_button) {
    itemButton.setOnClickListener { onClick() }

    bind {
        itemButton.text = item.text
    }
}