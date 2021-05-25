package com.lacker.staff.data.dto.auth

import com.lacker.dto.common.IdOwner
import com.lacker.staff.data.storage.user.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "family_name") val surname: String,
    @Json(name = "email") val email: String,
    @Json(name = "access_token") val token: String,
    @Json(name = "restaurant") val restaurant: IdOwner,
)

fun UserDto.toDomain() = User(
    id = id,
    name = name,
    surname = surname,
    email = email,
    token = token,
    restaurantId = restaurant.id,
)