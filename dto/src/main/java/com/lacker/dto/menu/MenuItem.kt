package com.lacker.dto.menu

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MenuItem(
    @Json(name = "id") val id: String,
    @Json(name = "title") val name: String,
    @Json(name = "image") val photoFullUrl: String,
    @Json(name = "description") val shortDescription: String,
    @Json(name = "portions") val portions: List<Portion>,
    @Json(name = "tags") val tags: List<DishTag>,
    @Json(name = "stopped") val stopped: Boolean,
)