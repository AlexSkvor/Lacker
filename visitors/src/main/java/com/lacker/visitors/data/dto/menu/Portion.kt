package com.lacker.visitors.data.dto.menu

import com.lacker.visitors.data.dto.order.SubOrder
import com.lacker.visitors.features.session.common.DomainPortion
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Portion(
    @Json(name = "id") val id: String,
    @Json(name = "price") val price: Int,
    @Json(name = "title") val portionName: String,
    @Json(name = "weight") val weight: Int
)

@Deprecated(message = "Tmp while server not fixed, replace later with Int")
@JsonClass(generateAdapter = true)
data class PriceTmp(
    @Json(name = "RUB") val rub: Double,
)

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