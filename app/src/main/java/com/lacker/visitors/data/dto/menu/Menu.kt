package com.lacker.visitors.data.dto.menu

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class Menu(
    @Json(name = "timeStamp") val timeStamp: LocalDateTime,
    @Json(name = "items") val items: List<MenuItem>
)
