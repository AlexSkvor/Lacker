package com.lacker.staff.data.dto.calls

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StaffCall(
    val tableName: String,
    val clientName: String,
    val callType: CallType
)

enum class CallType {
    PAY_CASH, PAY_CARD, CONSULTATION, UNKNOWN
}
