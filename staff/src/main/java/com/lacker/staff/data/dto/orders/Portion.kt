package com.lacker.staff.data.dto.orders

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Portion(
    @Json(name = "id") val id: String,
    @Json(name = "price") val price: Int,
    @Json(name = "name") val portionName: String,
    @Json(name = "count") val count: Int,
)