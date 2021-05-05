package com.lacker.staff.data.dto.orders

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderedDish(
    @Json(name = "id") val dishId: String,
    @Json(name = "name") val dishName: String,
    @Json(name = "portions") val portions: List<Portion>,
)