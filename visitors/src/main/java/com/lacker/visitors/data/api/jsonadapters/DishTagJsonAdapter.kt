package com.lacker.visitors.data.api.jsonadapters

import com.lacker.dto.menu.DishTag
import com.lacker.dto.menu.DishTag.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class DishTagJsonAdapter {

    private val map = listOf(
        ALCOHOL to "ALCOHOL",
        DISH to "DISH",
        DRINKS to "DRINKS",
        BIRD to "BIRD",
        SEA to "SEA",
        MEAT to "MEAT",
        GARNISH to "GARNISH",
        ASIAN_DISH to "ASIAN_DISH",
        DESSERT to "DESSERT",
        SALAD to "SALAD",
        SANDWICH to "SANDWICH",
        SOUP to "SOUP",
        OTHER to "OTHER",
    )

    @ToJson
    fun toJson(value: DishTag): String {
        return requireNotNull(map.firstOrNull { it.first == value }?.second) {
            "toJson($value) is not implemented yet!"
        }
    }

    @FromJson
    fun fromJson(value: String): DishTag = map.firstOrNull { it.second == value }?.first ?: OTHER
}