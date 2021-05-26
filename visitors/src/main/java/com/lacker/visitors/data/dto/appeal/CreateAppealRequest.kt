package com.lacker.visitors.data.dto.appeal

import com.lacker.dto.appeal.AppealType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateAppealRequest(
    @Json(name = "table") val tableId: String,
    @Json(name = "target") val target: AppealType,
)