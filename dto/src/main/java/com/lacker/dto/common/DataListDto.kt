package com.lacker.dto.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataListDto<T : Any>(
    @Json(name = "data") val data: List<T>,
)