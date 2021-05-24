package com.lacker.dto.menu

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Portion(
    @Json(name = "id") val id: String,
    @Json(name = "price") val price: Int,
    @Json(name = "title") val portionName: String,
    @Json(name = "sort") val sort: Int,
)