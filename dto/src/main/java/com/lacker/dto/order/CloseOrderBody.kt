package com.lacker.dto.order

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CloseOrderBody(
    val status: String = "PAID",
)