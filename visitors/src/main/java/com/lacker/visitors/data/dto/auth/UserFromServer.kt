package com.lacker.visitors.data.dto.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.lacker.visitors.data.storage.user.User

@JsonClass(generateAdapter = true)
data class UserFromServer(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String,
    @Json(name = "name") val name: String,
    @Json(name = "family_name") val surname: String?,
    @Json(name = "access_token") val token: String,
    @Json(name = "fullPhotoUrl") val fullPhotoUrl: String?
)

fun UserFromServer.toDomainUser(): User = User(
    id = id,
    name = name,
    surname = surname.orEmpty(),
    email = email,
    token = token,
    fullPhotoUrl = fullPhotoUrl ?: "https://i.ytimg.com/vi/Yh5whB-37HY/hqdefault_live.jpg",
)