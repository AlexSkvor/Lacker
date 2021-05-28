package com.lacker.dto.order

import com.lacker.dto.common.IdOwner
import com.lacker.dto.common.NameSurnameOwner
import com.lacker.dto.common.TitleOwner
import com.lacker.dto.menu.Portion
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class SubOrder(
    @Json(name = "id") val id: String,
    @Json(name = "comment") val comment: String?,
    @Json(name = "drinks_immediately") val drinksImmediately: Boolean = false,
    @Json(name = "portions") val portions: List<Portion>,
    @Json(name = "count") val totalPrice: Int,
    @Json(name = "create_time") val createdTimeStamp: OffsetDateTime?,
    @Json(name = "checked") val checked: Boolean,
    @Json(name = "table") val table: TitleOwner,
    @Json(name = "user") val user: NameSurnameOwner,
    @Json(name = "order") val fullOrder: IdOwner,
) {
    val orderList: List<OrderInfo>
        get() = portions.groupBy { it.id }
            .toList()
            .map { OrderInfo(portionId = it.first, ordered = it.second.size) }

    val clientName: String = user.name

    val tableName: String = table.title
}