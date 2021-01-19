package com.lacker.visitors.data.dto.menu

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MenuItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "photoFullUrl") val photoFullUrl: String,
    @Json(name = "shortDescription") val shortDescription: String,
    @Json(name = "portions") val portions: List<Portion>
)
