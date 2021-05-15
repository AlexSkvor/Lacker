package com.lacker.utils.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiException(
    @Json(name = "trace") val stackTrace: String?,
    @Json(name = "message") val message: String,
    @Json(name = "code") val code: Int,
)