package com.lacker.staff.features.auth.list

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.staff.R
import kotlinx.android.synthetic.main.item_header.*

fun getHeaderAdapter(
) = adapterDelegateLayoutContainer<RestaurantsListItem.Header, RestaurantsListItem>(
    R.layout.item_header
) {
    bind {
        header.text = getString(R.string.selectRestaurantHeader)
    }
}