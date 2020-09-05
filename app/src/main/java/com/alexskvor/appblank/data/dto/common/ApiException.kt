package com.alexskvor.appblank.data.dto.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiException(
    @Json(name = "name") val name: String,
    @Json(name = "message") val message: String,
    @Json(name = "code") val code: Int
)