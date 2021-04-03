package com.lacker.staff.data.dto.calls

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StaffCall(
    @Json(name = "id") val id: String,
    @Json(name = "tableName") val tableName: String,
    @Json(name = "clientName") val clientName: String,
    @Json(name = "callType") val callType: CallType
)

enum class CallType {
    PAY_CASH, PAY_CARD, CONSULTATION, UNKNOWN
}
