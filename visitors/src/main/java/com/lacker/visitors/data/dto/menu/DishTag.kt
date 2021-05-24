package com.lacker.visitors.data.dto.menu

import androidx.annotation.StringRes
import com.lacker.visitors.R

enum class DishTag(@StringRes val stringResource: Int) {

    ALCOHOL(R.string.tagAlcohol),
    DISH(R.string.tagDish),
    DRINKS(R.string.tagDrinks),
    BIRD(R.string.tagBird),
    SEA(R.string.tagSea),
    MEAT(R.string.tagMeat),
    GARNISH(R.string.tagGarnish),
    ASIAN_DISH(R.string.tagAsianDish),
    DESSERT(R.string.tagDessert),
    SALAD(R.string.tagSalad),
    SANDWICH(R.string.tagSandwich),
    SOUP(R.string.tagSoup),
    OTHER(R.string.tagOther),

}