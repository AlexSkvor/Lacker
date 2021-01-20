package com.lacker.visitors.data.dto.menu

import com.lacker.visitors.features.menu.DomainPortion
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Portion(
    @Json(name = "id") val id: String,
    @Json(name = "price") val price: Int,
    @Json(name = "portionName") val portionName: String
)

fun Portion.toDomain(menuItemId: String, orders: List<OrderInfo>) = DomainPortion(
    id = id,
    menuItemId = menuItemId,
    price = price,
    portionName = portionName,
    orderedNumber = orders.firstOrNull { it.portionId == id }?.ordered ?: 0
)