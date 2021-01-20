package com.lacker.visitors.features.menu

data class DomainPortion(
    val id: String,
    val menuItemId: String,
    val price: Int,
    val portionName: String,
    val orderedNumber: Int
)
