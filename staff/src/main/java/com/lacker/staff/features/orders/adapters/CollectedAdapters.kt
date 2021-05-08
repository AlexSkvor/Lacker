package com.lacker.staff.features.orders.adapters

import com.lacker.staff.data.dto.orders.SubOrderListItem

fun getNewOrdersAdaptersList(
    onViewClick: (SubOrderListItem) -> Unit,
    onAcceptClick: (SubOrderListItem) -> Unit,
) = listOf(
    orderAdapter(onViewClick = onViewClick, onAcceptClick = onAcceptClick),
)