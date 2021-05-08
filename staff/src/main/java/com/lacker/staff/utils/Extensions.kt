package com.lacker.staff.utils

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import voodoo.rocks.paginator.PaginationView

fun PaginationView.addOrReplaceExistingAdapters(adaptersList: List<AdapterDelegate<List<Any>>>) {
    if (adaptersList.isNotEmpty())
        addOrReplaceExistingAdapter(
            mustAdapter = adaptersList[0],
            adapters = adaptersList.subList(1, adaptersList.size).toTypedArray(),
        )
}