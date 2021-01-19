package com.lacker.visitors.data.dto.menu

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Portion(
    @Json(name = "id") val id: String,
    @Json(name = "price") val price: Int,
    @Json(name = "portionName") val portionName: String
)
