package com.lacker.staff.data.dto.restaurant

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RestaurantInfoResponse(
    @Json(name = "restaurant") val restaurant: RestaurantDto? = null
)
