package com.lacker.staff.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lacker.staff.R
import com.lacker.staff.data.dto.orders.DomainPortion
import com.lacker.utils.extensions.asMoney
import com.lacker.utils.extensions.visible
import kotlinx.android.synthetic.main.view_portion.view.*

class PortionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.view_portion, this)
    }

    fun setupForPortion(
        showNumber: Boolean,
        portion: DomainPortion,
    ) {
        portionDescription.text = portion.portionName
        orderedPortionsNumber.text = context.getString(R.string.orderedCount, portion.count)
        portionPrice.text = resources.getString(R.string.rurSymbolPrice, portion.price.asMoney())

        orderedPortionsNumber.visible = showNumber
    }

}