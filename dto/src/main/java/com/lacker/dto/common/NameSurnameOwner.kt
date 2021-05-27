package com.lacker.dto.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NameSurnameOwner(
    @Json(name = "name") val firstName: String,
    @Json(name = "family_name") val surname: String,
) {
    val name = "$firstName $surname"
}
