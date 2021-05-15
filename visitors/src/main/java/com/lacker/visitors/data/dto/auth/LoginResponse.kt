package com.lacker.visitors.data.dto.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "data") val user: UserFromServer
)