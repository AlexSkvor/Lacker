package com.lacker.staff.data.dto.orders

import java.io.Serializable
import java.time.OffsetDateTime

data class SubOrderListItem(
    val id: String,
    val clientName: String,
    val tableName: String,
    val createdDateTime: OffsetDateTime?,
    val comment: String,
    val orderList: List<Dish>,
    val fullOrderId: String,
): Serializable