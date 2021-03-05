package com.lacker.staff.data.dto.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthRequest(
    @Json(name = "restaurantId") val restaurantId: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)
