package com.lacker.visitors.features.session.common

data class DomainPortion(
    val id: String,
    val menuItemId: String,
    val price: Int,
    val portionName: String,
    val basketNumber: Int,
    val orderedNumber: Int // TODO needs different counters for Ordered and Basket!
)
