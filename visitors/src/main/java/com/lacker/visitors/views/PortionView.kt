package com.lacker.visitors.views

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lacker.utils.extensions.asMoney
import com.lacker.utils.extensions.visible
import com.lacker.visitors.R
import com.lacker.visitors.data.storage.basket.BasketManager.Companion.MAX_BASKET_SIZE_FOR_ONE_MENU_ITEM
import com.lacker.visitors.features.session.common.DomainPortion
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
        dishStopped: Boolean,
        portion: DomainPortion,
        onAddToOrder: (DomainPortion) -> Unit,
        onAddToBasket: (DomainPortion) -> Unit,
        removeFromBasket: (DomainPortion) -> Unit,
        allowChanges: Boolean = true
    ) {
        portionDescription.text = portion.portionName
        orderedPortionsNumber.text = portion.getOrderedNumberString()
        portionPrice.text = resources.getString(R.string.rurSymbolPrice, portion.price.asMoney())

        minusPortionButton.visible = portion.basketNumber + portion.orderedNumber > 0
        orderedPortionsNumber.visible = portion.basketNumber + portion.orderedNumber > 0

        minusPortionButton.isEnabled = portion.basketNumber > 0 && allowChanges && !dishStopped
        plusPortionButton.isEnabled = allowChanges && !dishStopped &&
                portion.basketNumber + portion.orderedNumber < MAX_BASKET_SIZE_FOR_ONE_MENU_ITEM

        minusPortionButton.setOnClickListener { removeFromBasket(portion) }
        plusPortionButton.setOnClickListener { onAddToBasket(portion) }
        plusPortionButton.setOnLongClickListener {
            onAddToOrder(portion)
            true
        }

        portionContainer.setOnClickListener { }
    }

    private fun DomainPortion.getOrderedNumberString(): CharSequence {
        if (orderedNumber == 0) return basketNumber.toString()

        val text = if (basketNumber > 0) "$orderedNumber + $basketNumber"
        else orderedNumber.toString()

        val spannable = SpannableString(text)
        val index = text.indexOf(orderedNumber.toString())
        spannable.setSpan(
            ForegroundColorSpan(Color.GRAY),
            index,
            index + orderedNumber.toString().length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannable
    }

}