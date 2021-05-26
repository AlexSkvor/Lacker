package com.lacker.staff.data.dto.orders

import java.time.OffsetDateTime

data class SubOrderListItem(
    val id: String,
    val clientName: String,
    val tableName: String,
    val createdDateTime: OffsetDateTime,
    val comment: String,
    val orderList: List<OrderedDish>,
)