package com.lacker.visitors.data.dto.menu

import com.lacker.dto.menu.Portion
import com.lacker.visitors.data.dto.order.SubOrder
import com.lacker.visitors.features.session.common.DomainPortion

fun Portion.toDomain(
    menuItemId: String,
    orders: List<SubOrder>,
    basket: List<OrderInfo>
) = DomainPortion(
    id = id,
    menuItemId = menuItemId,
    price = price,
    portionName = portionName,
    basketNumber = basket.firstOrNull { it.portionId == id }?.ordered ?: 0,
    orderedNumber = orders.map { it.orderList }
        .flatten()
        .filter { it.portionId == id }
        .sumOf { it.ordered }
)