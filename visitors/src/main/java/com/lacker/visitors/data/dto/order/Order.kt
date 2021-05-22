package com.lacker.visitors.data.dto.order

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Order(
    @Json(name = "id") val id: String,
    @Json(name = "status") val status: String,
    @Json(name = "sub_orders") val subOrders: List<SubOrder>
)