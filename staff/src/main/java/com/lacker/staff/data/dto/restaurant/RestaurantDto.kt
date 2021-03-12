package com.lacker.staff.data.dto.restaurant

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RestaurantDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "fullPhotoUrl") val fullPhotoUrl: String,
    @Json(name = "addressString") val addressString: String
)