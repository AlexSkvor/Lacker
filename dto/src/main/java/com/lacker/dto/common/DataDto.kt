package com.lacker.dto.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataDto<T : Any>(
    @Json(name = "data") val data: T,
)
