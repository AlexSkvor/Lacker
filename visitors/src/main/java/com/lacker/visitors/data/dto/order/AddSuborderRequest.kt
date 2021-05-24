package com.lacker.visitors.data.dto.order

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddSuborderRequest(
    @Json(name = "comment") val comment: String,
    @Json(name = "drinks_immediately") val drinksImmediately: Boolean,
    @Json(name = "portions") val portions: List<String>,
)