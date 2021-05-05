package com.lacker.staff.data.dto.orders

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewOrdersPageResponse(
    @Json(name = "subOrders") val subOrders: List<SubOrderListItem>
)
