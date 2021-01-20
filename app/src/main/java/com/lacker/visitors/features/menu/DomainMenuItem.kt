package com.lacker.visitors.features.menu

data class DomainMenuItem(
    val id: String,
    val name: String,
    val photoFullUrl: String,
    val shortDescription: String,
    val portions: List<DomainPortion>
)
