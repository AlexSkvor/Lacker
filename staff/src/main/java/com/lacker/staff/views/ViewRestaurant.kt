package com.lacker.staff.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.lacker.staff.R
import com.lacker.staff.data.dto.restaurant.RestaurantDto
import com.lacker.utils.extensions.*
import kotlinx.android.synthetic.main.view_restaurant.view.*

/**
 * Be careful, this view does not
 * accept respect parameters:
 * Paddings;
 * Background;
 * Foreground!
 */
class ViewRestaurant @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var restaurant: RestaurantDto? = null
        set(value) {
            field = value
            if (isLaidOut) render(value)
        }

    init {
        inflate(context, R.layout.view_restaurant, this)

        background = drawableCompat(R.drawable.bg_white_rounded_corners)
        elevation = resources.getDimension(R.dimen._8sdp)
        setAllPaddings(R.dimen._8sdp)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            addDefaultSelectableAnimation()

        if (!isInEditMode)
            render(restaurant)
    }

    private fun render(rest: RestaurantDto?) {
        if (rest == null) renderEmpty()
        else renderRestaurant(rest)
    }

    private fun renderEmpty() {
        restaurantPhoto.loadDrawableRes(R.drawable.ic_baseline_find_replace_24)
        restaurantName.text = resources.getString(R.string.restaurant)
        restaurantAddress.text = resources.getString(R.string.noSelectedRestaurant)
    }

    private fun renderRestaurant(restaurant: RestaurantDto) {
        restaurantPhoto.loadFromNet(restaurant.fullPhotoUrl)
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
        restaurantName.setTextColor(getColor(R.color.brown))
        restaurantAddress.setTextColor(getColor(R.color.brown))
    }

    private fun recolorToInactive() {
        isClickable = false
        restaurantName.setTextColor(getColor(R.color.darkGrey))
        restaurantAddress.setTextColor(getColor(R.color.darkGrey))

    }
}