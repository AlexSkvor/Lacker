package com.lacker.visitors.data.dto.menu

import com.lacker.visitors.data.dto.order.SubOrder
import com.lacker.visitors.features.session.common.DomainMenuItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MenuItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "photoFullUrl") val photoFullUrl: String,
    @Json(name = "shortDescription") val shortDescription: String,
    @Json(name = "portions") val portions: List<Portion>
)

fun MenuItem.toDomain(
    orders: List<SubOrder>,
    basket: List<OrderInfo>,
    favourites: Set<String>
) = DomainMenuItem(
    id = id,
    name = name,
    photoFullUrl = photoFullUrl,
    shortDescription = shortDescription,
    portions = portions.map { it.toDomain(id, orders, basket) },
    inFavourites = id in favourites
)