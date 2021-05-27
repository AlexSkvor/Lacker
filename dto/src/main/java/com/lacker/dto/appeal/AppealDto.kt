package com.lacker.dto.appeal

import com.lacker.dto.common.NameOwner
import com.lacker.dto.common.TitleOwner
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class AppealDto(
    @Json(name = "table") val table: TitleOwner,
    @Json(name = "user") val user: NameOwner,
    @Json(name = "target") val target: AppealType,
    @Json(name = "checked") val checked: Boolean,
    @Json(name = "id") val id: String,
    @Json(name = "create_time") val created: OffsetDateTime,
)