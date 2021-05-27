package com.lacker.staff.features.orders.adapters

import com.lacker.dto.appeal.AppealDto
import com.lacker.staff.data.dto.orders.SubOrderListItem

fun getOrdersAdaptersList(
    onViewClick: (SubOrderListItem) -> Unit,
    acceptInsteadView: Boolean,
    onRefresh: () -> Unit,
) = listOf(
    orderAdapter(onViewClick = onViewClick, acceptInsteadView),
    customEmptyListAdapter(onRefresh)
)

fun getAppealsAdaptersList(
    onAccept: (AppealDto) -> Unit,
    canAccept: Boolean,
    onRefresh: () -> Unit,
) = listOf(
    appealAdapter(canAccept = canAccept, onAccept = onAccept),
    customEmptyListAdapter(onRefresh),
)