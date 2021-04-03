package com.lacker.staff.data.dto.orders

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewOrdersPageRequest(
    @Json(name = "totalNewOrders") val totalNewOrders: Int,
    @Json(name = "ordersBeforeFirst") val ordersBeforeFirst: Int,
    @Json(name = "subOrders") val subOrders: List<SubOrderListItem>
)
