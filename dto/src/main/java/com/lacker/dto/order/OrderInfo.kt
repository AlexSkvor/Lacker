package com.lacker.dto.order

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderInfo(
    @Json(name = "portionId") val portionId: String,
    @Json(name = "ordered") val ordered: Int
)