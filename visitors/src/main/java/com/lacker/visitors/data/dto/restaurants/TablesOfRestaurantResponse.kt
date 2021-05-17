package com.lacker.visitors.data.dto.restaurants

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TablesOfRestaurantResponse(
    @Json(name = "data") val data: List<TableDto>,
)

@JsonClass(generateAdapter = true)
data class TableDto(
    @Json(name = "id") val id: String,
)