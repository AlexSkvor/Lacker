package com.lacker.visitors.data.dto.menu

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class MenuResponse(
    @Json(name = "data") val menu: Menu,
)

@JsonClass(generateAdapter = true)
data class Menu(
    @Json(name = "update_date") val timeStamp: OffsetDateTime,
    @Json(name = "dishes") val items: List<MenuItem>
)
