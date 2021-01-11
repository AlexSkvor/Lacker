package com.lacker.visitors.data.dto.menu

data class MenuItem(
    val id: String,
    val name: String,
    val photoFullUrl: String,
    val shortDescription: String,
    val portions: List<Portion>
)
