package com.lacker.dto.jsonadapters

import com.lacker.dto.order.OrderStatus
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class OrderStatusJsonAdapter {

    private val map = listOf(
        OrderStatus.NEW to "NEW",
        OrderStatus.PAID to "PAID",
        OrderStatus.CANCELLED to "CANCELLED",
        OrderStatus.UNKNOWN to "",
    )

    @ToJson
    fun toJson(value: OrderStatus): String {
        return requireNotNull(map.firstOrNull { it.first == value }?.second) {
            "toJson($value) is not implemented yet!"
        }
    }

    @FromJson
    fun fromJson(value: String): OrderStatus = map.firstOrNull { it.second == value }?.first
        ?: OrderStatus.UNKNOWN

}