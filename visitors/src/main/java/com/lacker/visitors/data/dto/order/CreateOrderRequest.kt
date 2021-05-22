package com.lacker.visitors.data.dto.order

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateOrderRequest(
    @Json(name = "restaurant") val restaurantId: String,
)