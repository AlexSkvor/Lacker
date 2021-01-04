package com.lacker.visitors.data.dto.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "exception") val exception: ApiException? = null
)