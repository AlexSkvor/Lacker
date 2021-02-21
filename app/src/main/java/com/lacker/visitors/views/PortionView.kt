package com.lacker.visitors.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lacker.utils.extensions.visible
import com.lacker.visitors.R
import com.lacker.visitors.data.storage.basket.BasketManager.Companion.MAX_BASKET_SIZE_FOR_ONE_MENU_ITEM
import com.lacker.visitors.features.session.common.DomainPortion
import com.lacker.visitors.utils.asMoney
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
        portion: DomainPortion,
        onAddToOrder: (DomainPortion) -> Unit,
        onAddToBasket: (DomainPortion) -> Unit,
        removeFromBasket: (DomainPortion) -> Unit,
    ) {
        portionDescription.text = portion.portionName
        orderedPortionsNumber.text = (portion.basketNumber + portion.orderedNumber).toString()
        portionPrice.text = resources.getString(R.string.rurSymbolPrice, portion.price.asMoney())

        minusPortionButton.visible = portion.basketNumber + portion.orderedNumber > 0
        orderedPortionsNumber.visible = portion.basketNumber + portion.orderedNumber > 0

        minusPortionButton.isEnabled = portion.basketNumber > 0
        plusPortionButton.isEnabled = portion.basketNumber + portion.orderedNumber < MAX_BASKET_SIZE_FOR_ONE_MENU_ITEM

        minusPortionButton.setOnClickListener { removeFromBasket(portion) }
        plusPortionButton.setOnClickListener { onAddToBasket(portion) }
        plusPortionButton.setOnLongClickListener {
            onAddToOrder(portion)
            true
        }

        portionContainer.setOnClickListener { onAddToBasket(portion) }
        portionContainer.setOnLongClickListener {
            onAddToOrder(portion)
            true
        }
    }


}