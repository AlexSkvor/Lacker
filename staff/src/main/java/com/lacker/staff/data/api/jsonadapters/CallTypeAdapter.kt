package com.lacker.staff.data.api.jsonadapters

import com.lacker.staff.data.dto.calls.CallType
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class CallTypeAdapter {

    private val map = mapOf(
        "consultation" to CallType.CONSULTATION,
        "cash_payment" to CallType.PAY_CASH,
        "bank_payment" to CallType.PAY_CARD
    )

    @ToJson
    fun toJson(value: CallType): String = map.entries
        .firstOrNull { it.value == value }?.key.orEmpty()

    @FromJson
    fun fromJson(value: String): CallType = map[value] ?: CallType.UNKNOWN

}