package com.lacker.staff.data.dto.orders

import com.lacker.dto.order.SubOrder
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubOrdersListResponse(
    @Json(name = "data") val data: List<SubOrder>,
)
