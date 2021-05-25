package com.lacker.visitors.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lacker.utils.extensions.gone
import com.lacker.utils.extensions.visible
import com.lacker.visitors.R
import com.lacker.visitors.features.session.common.DomainMenuItem
import com.lacker.visitors.features.session.common.DomainPortion
import kotlinx.android.synthetic.main.view_small_menu_item.view.*

class SmallMenuItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.view_small_menu_item, this)
    }

    fun setupForMenuItem(
        index: Int,
        item: DomainMenuItem,
        onAddToBasket: (DomainPortion) -> Unit,
        removeFromBasket: (DomainPortion) -> Unit,
        allowChanges: Boolean = true
    ) {
        divider.visible = index != 0
        menuItemName.text = item.name
        val filtered = item.portions.filter { it.basketNumber >= 1 }
        filtered.forEachIndexed { i, portion ->
            if (smallMenuItemContainer.childCount <= i)
                smallMenuItemContainer.addView(PortionView(context))
            val portionView = smallMenuItemContainer.getChildAt(i) as PortionView
            portionView.apply {
                setupForPortion(
                    dishStopped = item.stopped,
                    portion = portion,
                    onAddToOrder = {},
                    onAddToBasket = onAddToBasket,
                    removeFromBasket = removeFromBasket,
                    allowChanges = allowChanges
                )
                visible()
            }
        }
        for (i in filtered.size until smallMenuItemContainer.childCount)
            smallMenuItemContainer.getChildAt(i).gone()
    }

}