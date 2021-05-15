package com.lacker.staff.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView
import com.lacker.staff.R
import com.lacker.staff.data.dto.restaurant.RestaurantDto
import com.lacker.utils.extensions.*
import kotlinx.android.synthetic.main.view_restaurant.view.*

class ViewRestaurant @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    var restaurant: RestaurantDto? = null
        set(value) {
            field = value
            if (restaurantPhoto != null) render(value)
        }

    init {
        inflate(context, R.layout.view_restaurant, this)

        cardElevation = resources.getDimension(R.dimen._8sdp)
        radius = resources.getDimension(R.dimen._8sdp)

        if (!isInEditMode)
            render(restaurant)
    }

    private fun render(rest: RestaurantDto?) {
        if (rest == null) renderEmpty()
        else renderRestaurant(rest)
    }

    private fun renderEmpty() {
        restaurantPhoto.loadDrawableRes(R.drawable.ic_baseline_find_replace_24, crossFade = false)
        restaurantName.text = resources.getString(R.string.restaurant)
        restaurantAddress.text = resources.getString(R.string.noSelectedRestaurant)
    }

    private fun renderRestaurant(restaurant: RestaurantDto) {
        restaurantPhoto.loadFromNet(restaurant.fullPhotoUrl, crossFade = false)
        restaurantName.text = restaurant.name
        restaurantAddress.text = restaurant.addressString
    }

    var active: Boolean = true
        set(value) {
            field = value
            if (value) recolorToActive() else recolorToInactive()
        }

    private fun recolorToActive() {
        isClickable = true
        restaurantName.setTextColor(getColor(R.color.blueSuperDark))
        restaurantAddress.setTextColor(getColor(R.color.blueSuperDark))
    }

    private fun recolorToInactive() {
        isClickable = false
        restaurantName.setTextColor(getColor(R.color.darkGrey))
        restaurantAddress.setTextColor(getColor(R.color.darkGrey))

    }
}