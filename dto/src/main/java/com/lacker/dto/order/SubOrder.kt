package com.lacker.dto.order

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class SubOrder(
    @Json(name = "comment") val comment: String,
    @Json(name = "drinksImmediately") val drinksImmediately: Boolean = false,
    @Json(name = "orderList") val orderList: List<OrderInfo>,
    @Json(name = "createdTimeStamp") val createdTimeStamp: OffsetDateTime?
)