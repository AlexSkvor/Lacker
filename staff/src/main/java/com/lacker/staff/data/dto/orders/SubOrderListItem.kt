package com.lacker.staff.data.dto.orders

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class SubOrderListItem(
    @Json(name = "id") val id: String,
    @Json(name = "clientName") val clientName: String,
    @Json(name = "tableName") val tableName: String,
    @Json(name = "createdDateTime") val createdDateTime: OffsetDateTime,
    @Json(name = "comment") val comment: String,
    @Json(name = "portionsNumber") val portionsNumber: Int
)
