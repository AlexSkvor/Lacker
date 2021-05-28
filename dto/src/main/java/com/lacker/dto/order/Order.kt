package com.lacker.dto.order

import com.lacker.dto.common.NameSurnameOwner
import com.lacker.dto.common.TitleOwner
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class Order(
    @Json(name = "id") val id: String,
    @Json(name = "status") val status: OrderStatus,
    @Json(name = "sub_orders") val subOrders: List<SubOrder>,
    @Json(name = "user") val user: NameSurnameOwner,
    @Json(name = "table") val table: TitleOwner,
    @Json(name = "create_time") val created: OffsetDateTime,
)