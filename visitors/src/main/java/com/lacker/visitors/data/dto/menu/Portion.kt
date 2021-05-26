package com.lacker.visitors.data.dto.menu

import com.lacker.dto.menu.Portion
import com.lacker.dto.order.OrderInfo
import com.lacker.dto.order.SubOrder
import com.lacker.visitors.features.session.common.DomainPortion

fun Portion.toDomain(
    orders: List<SubOrder>,
    basket: List<OrderInfo>
) = DomainPortion(
    id = id,
    price = price,
    portionName = portionName,
    basketNumber = basket.firstOrNull { it.portionId == id }?.ordered ?: 0,
    orderedNumber = orders.map { it.orderList }
        .flatten()
        .filter { it.portionId == id }
        .sumOf { it.ordered }
)