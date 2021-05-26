package com.lacker.staff.data.dto.orders

import com.lacker.dto.menu.DishTag
import java.io.Serializable

data class Dish(
    val dishId: String,
    val dishName: String,
    val portions: List<DomainPortion>,
    val shortDescription: String,
    val photoFullUrl: String,
    val tags: List<DishTag>,
    val stopped: Boolean,
): Serializable