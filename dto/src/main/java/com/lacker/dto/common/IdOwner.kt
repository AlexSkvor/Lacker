package com.lacker.dto.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IdOwner(
    @Json(name = "id") val id: String,
)
