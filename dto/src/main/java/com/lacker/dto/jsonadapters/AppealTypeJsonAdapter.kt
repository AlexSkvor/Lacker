package com.lacker.dto.jsonadapters

import com.lacker.dto.appeal.AppealType
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class AppealTypeJsonAdapter {

    //TODO
    private val map = listOf(
        AppealType.CONSULTATION to "INFO",
        AppealType.PAYMENT_BANK to "TODO1",
        AppealType.PAYMENT_CASH to "TODO2",
        AppealType.UNKNOWN to "",
    )

    @ToJson
    fun toJson(value: AppealType): String {
        return requireNotNull(map.firstOrNull { it.first == value }?.second) {
            "toJson($value) is not implemented yet!"
        }
    }

    @FromJson
    fun fromJson(value: String): AppealType = map.firstOrNull { it.second == value }?.first
        ?: AppealType.UNKNOWN

}