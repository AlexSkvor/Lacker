package com.lacker.dto.jsonadapters

import com.lacker.dto.menu.DishTag
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class DishTagJsonAdapter {

    private val map = listOf(
        DishTag.ALCOHOL to "ALCOHOL",
        DishTag.DISH to "DISH",
        DishTag.DRINKS to "DRINKS",
        DishTag.BIRD to "BIRD",
        DishTag.SEA to "SEA",
        DishTag.MEAT to "MEAT",
        DishTag.GARNISH to "GARNISH",
        DishTag.ASIAN_DISH to "ASIAN_DISH",
        DishTag.DESSERT to "DESSERT",
        DishTag.SALAD to "SALAD",
        DishTag.SANDWICH to "SANDWICH",
        DishTag.SOUP to "SOUP",
        DishTag.OTHER to "OTHER",
    )

    @ToJson
    fun toJson(value: DishTag): String {
        return requireNotNull(map.firstOrNull { it.first == value }?.second) {
            "toJson($value) is not implemented yet!"
        }
    }

    @FromJson
    fun fromJson(value: String): DishTag = map.firstOrNull { it.second == value }?.first ?: DishTag.OTHER
}