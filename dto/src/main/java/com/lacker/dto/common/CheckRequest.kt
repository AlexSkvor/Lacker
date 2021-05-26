package com.lacker.dto.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckRequest(
    @Json(name = "checked") val checked: Boolean = true
)
