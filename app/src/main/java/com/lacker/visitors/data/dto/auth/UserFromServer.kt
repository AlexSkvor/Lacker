package com.lacker.visitors.data.dto.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.lacker.visitors.data.storage.User

@JsonClass(generateAdapter = true)
data class UserFromServer(
    @Json(name = "id") val id: Long,
    @Json(name = "email") val email: String,
    @Json(name = "fullName") val fullName: String,
    @Json(name = "role") val role: User.Role,
    @Json(name = "isActive") val active: Boolean,
    @Json(name = "accessToken") val token: String,
    @Json(name = "city") val city: String
)

fun UserFromServer.toDomainUser(): User = User(
    id = id,
    token = token,
    role = role,
    email = email,
    fullName = fullName,
    city = city
)