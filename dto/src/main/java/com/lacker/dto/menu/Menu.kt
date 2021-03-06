package com.lacker.dto.menu

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class Menu(
    @Json(name = "update_time") val timeStamp: OffsetDateTime,
    @Json(name = "dishes") val items: List<MenuItem>
)