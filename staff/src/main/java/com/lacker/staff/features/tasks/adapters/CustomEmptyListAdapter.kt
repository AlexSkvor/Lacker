package com.lacker.staff.features.tasks.adapters

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.staff.R
import kotlinx.android.synthetic.main.item_empty_list.*
import voodoo.rocks.paginator.adapters.EmptyItem

// TODO other custom adapters
fun customEmptyListAdapter(
    onRefresh: () -> Unit,
) = adapterDelegateLayoutContainer<EmptyItem, Any>(R.layout.item_empty_list) {

    refreshButton.setOnClickListener { onRefresh() }

    bind {
        emptyMessageText.text = getString(R.string.emptyText)
    }

}