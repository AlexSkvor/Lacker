package com.lacker.visitors.data.dto.order

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Order(
    @Json(name = "status") val status: String,
    @Json(name = "subOrders") val subOrders: List<SubOrder>
)