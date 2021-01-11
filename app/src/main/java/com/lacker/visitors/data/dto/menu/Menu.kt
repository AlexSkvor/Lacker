package com.lacker.visitors.data.dto.menu

import java.time.LocalDateTime

data class Menu(
    val timeStamp: LocalDateTime,
    val items: List<MenuItem>
)
