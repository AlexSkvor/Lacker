package com.lacker.dto.menu

data class MenuSearchFilter(
    val tags: Set<DishTag>,
    val text: String,
)