package com.lacker.staff.data.dto.orders

import java.io.Serializable

data class DomainPortion(
    val id: String,
    val price: Int,
    val portionName: String,
    val count: Int,
): Serializable