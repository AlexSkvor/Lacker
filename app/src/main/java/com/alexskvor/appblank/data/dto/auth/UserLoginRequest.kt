package com.alexskvor.appblank.data.dto.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserLoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)