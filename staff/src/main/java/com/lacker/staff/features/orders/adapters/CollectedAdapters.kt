package com.lacker.staff.features.orders.adapters

import com.lacker.staff.data.dto.orders.SubOrderListItem

fun getNewOrdersAdaptersList(
    onViewClick: (SubOrderListItem) -> Unit,
    onAcceptClick: (SubOrderListItem) -> Unit,
    onRefresh: () -> Unit,
) = listOf(
    orderAdapter(onViewClick = onViewClick, onAcceptClick = onAcceptClick),
    customEmptyListAdapter(onRefresh)
)