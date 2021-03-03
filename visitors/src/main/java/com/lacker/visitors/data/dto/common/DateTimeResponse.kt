package com.lacker.visitors.data.dto.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class DateTimeResponse(
    @Json(name = "dateTime") val dateTime: OffsetDateTime
)
