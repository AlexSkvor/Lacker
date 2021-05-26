package com.lacker.staff.features.orders.adapters

import com.lacker.staff.data.dto.orders.SubOrderListItem

fun getNewOrdersAdaptersList(
    onViewClick: (SubOrderListItem) -> Unit,
    acceptInsteadView: Boolean,
    onRefresh: () -> Unit,
) = listOf(
    orderAdapter(onViewClick = onViewClick, acceptInsteadView),
    customEmptyListAdapter(onRefresh)
)