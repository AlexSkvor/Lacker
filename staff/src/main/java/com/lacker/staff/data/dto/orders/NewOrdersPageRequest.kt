package com.lacker.staff.data.dto.orders

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewOrdersPageRequest(
    @Json(name = "lastSubOrderId") val lastSubOrderId: String?,
)
