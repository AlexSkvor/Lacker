package com.lacker.visitors.features.auth.main

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleAuthData(
    @Json(name = "googleId") val googleId: String,
    @Json(name = "name") val name: String,
    @Json(name = "surname") val surname: String,
    @Json(name = "email") val email: String
)
