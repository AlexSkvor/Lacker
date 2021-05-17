package com.lacker.visitors.data.dto.menu

import com.lacker.visitors.data.dto.order.SubOrder
import com.lacker.visitors.features.session.common.DomainMenuItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MenuItem(
    @Json(name = "id") val id: String,
    @Json(name = "title") val name: String,
    @Json(name = "image") val photoFullUrl: String,
    @Json(name = "description") val shortDescription: String,
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