package com.lacker.visitors.data.dto.order

import com.lacker.dto.order.Order
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentOrderResponse(
    @Json(name = "data") val order: Order,
)
