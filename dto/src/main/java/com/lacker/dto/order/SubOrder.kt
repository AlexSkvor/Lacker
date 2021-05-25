package com.lacker.dto.order

import com.lacker.dto.menu.Portion
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class SubOrder(
    @Json(name = "comment") val comment: String,
    @Json(name = "drinks_immediately") val drinksImmediately: Boolean = false,
    @Json(name = "portions") val portions: List<Portion>,
    @Json(name = "count") val totalPrice: Int,
    @Json(name = "createdTimeStamp") val createdTimeStamp: OffsetDateTime?,
) {
    val orderList: List<OrderInfo>
        get() = portions.groupBy { it.id }
            .toList()
            .map { OrderInfo(portionId = it.first, ordered = it.second.size) }
}