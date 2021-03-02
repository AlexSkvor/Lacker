package com.lacker.visitors.features.session.common

import java.io.Serializable

data class DomainPortion(
    val id: String,
    val menuItemId: String,
    val price: Int,
    val portionName: String,
    val basketNumber: Int,
    val orderedNumber: Int
): Serializable
