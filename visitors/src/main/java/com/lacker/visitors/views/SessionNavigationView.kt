package com.lacker.visitors.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import com.lacker.utils.extensions.*
import com.lacker.visitors.R
import com.lacker.visitors.features.session.menu.MenuMachine
import kotlinx.android.synthetic.main.view_session_navigation.view.*

class SessionNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val views by lazy {
        mutableListOf(
            menuTab to State.MENU,
            favouriteTab to State.FAVOURITE,
            basketTab to State.BASKET,
            orderTab to State.ORDER
        )
    }

    private var lastClickTime: Long = 0

    init {
        inflate(context, R.layout.view_session_navigation, this)

        views.forEach { pair ->
            pair.first.setOnClickListener {
                if (System.currentTimeMillis() - lastClickTime > 350) {
                    lastClickTime = System.currentTimeMillis()
                    stateListener?.invoke(pair.second)
                }
            }
        }
    }

    private var stateListener: ((State) -> Unit)? = null

    fun onStateChange(listener: (State) -> Unit) {
        stateListener = listener
    }

    fun setState(value: State) {
        setupButtonsForState(value)
    }

    private fun setupButtonsForState(state: State) {
        views.forEach {
            if (it.second == state) it.first.select()
            else it.first.unSelect()
        }
    }

    enum class State {
        MENU, FAVOURITE, BASKET, ORDER
    }

    fun setFavouriteBadge(value: Int) {
        favouriteBadge.visible = value > 0
        favouriteBadge.text = value.toString()
    }

    fun setBasketBadge(value: Int) {
        basketBadge.visible = value > 0
        basketBadge.text = value.toString()
    }

    fun setOrderBadge(value: Int) {
        orderBadge.visible = value > 0
        orderBadge.text = value.toString()
    }

}

fun SessionNavigationView.State.asDomain() = when (this) {
    SessionNavigationView.State.MENU -> MenuMachine.State.Type.MENU
    SessionNavigationView.State.FAVOURITE -> MenuMachine.State.Type.FAVOURITE
    SessionNavigationView.State.BASKET -> MenuMachine.State.Type.BASKET
    SessionNavigationView.State.ORDER -> MenuMachine.State.Type.ORDER
}

fun MenuMachine.State.Type.asUi() = when (this) {
    MenuMachine.State.Type.MENU -> SessionNavigationView.State.MENU
    MenuMachine.State.Type.FAVOURITE -> SessionNavigationView.State.FAVOURITE
    MenuMachine.State.Type.BASKET -> SessionNavigationView.State.BASKET
    MenuMachine.State.Type.ORDER -> SessionNavigationView.State.ORDER
}

private fun TextView.select() {
    setTintColor(R.color.white)
    setTextColor(getColor(R.color.white))
    setTextSizeRes(R.dimen._8ssp)
}

private fun TextView.unSelect() {
    setTintColor(R.color.blueVeryLight)
    setTextColor(getColor(R.color.blueVeryLight))
    setTextSizeRes(R.dimen._7ssp)
}