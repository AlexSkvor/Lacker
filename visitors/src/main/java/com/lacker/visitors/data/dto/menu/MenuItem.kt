package com.lacker.visitors.data.dto.menu

import com.lacker.dto.menu.MenuItem
import com.lacker.dto.order.OrderInfo
import com.lacker.dto.order.SubOrder
import com.lacker.visitors.features.session.common.DomainMenuItem

fun MenuItem.toDomain(
    orders: List<SubOrder>,
    basket: List<OrderInfo>,
    favourites: Set<String>
) = DomainMenuItem(
    id = id,
    name = name,
    photoFullUrl = photoFullUrl,
    shortDescription = shortDescription,
    portions = portions.sortedBy { it.sort }.map { it.toDomain(id, orders, basket) },
    inFavourites = id in favourites,
    tags = tags.toSet(),
    stopped = stopped,
)