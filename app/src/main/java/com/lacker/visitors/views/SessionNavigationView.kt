package com.lacker.visitors.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import com.lacker.utils.extensions.getColor
import com.lacker.utils.extensions.setTextSizeRes
import com.lacker.utils.extensions.setTintColor
import com.lacker.visitors.R
import kotlinx.android.synthetic.main.view_session_navigation.view.*

class SessionNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private companion object {
        const val MIN_TIME_BETWEEN_CLICKS = 300
    }

    private var lastChangeTime: Long = 0L

    private val views by lazy {
        mutableListOf(
            menuTab to State.MENU,
            favouriteTab to State.FAVOURITE,
            basketTab to State.BASKET,
            orderTab to State.ORDER
        )
    }

    init {
        inflate(context, R.layout.view_session_navigation, this)

        views.forEach { pair ->
            pair.first.setOnClickListener {
                if (System.currentTimeMillis() - lastChangeTime > MIN_TIME_BETWEEN_CLICKS) {
                    lastChangeTime = System.currentTimeMillis()
                    if (state != pair.second) {
                        state = pair.second
                        stateListener?.invoke(pair.second)
                    }
                }
            }
        }
    }

    private var stateListener: ((State) -> Unit)? = null

    fun onStateChange(listener: (State) -> Unit) {
        stateListener = listener
    }

    var state: State? = null
        set(value) {
            field = value
            value?.let {
                setupButtonsForState(value)
            }
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

}

private fun TextView.select() {
    setTintColor(R.color.brownExtraLight)
    setTextColor(getColor(R.color.brownExtraLight))
    setTextSizeRes(R.dimen._8ssp)
}

private fun TextView.unSelect() {
    setTintColor(R.color.white)
    setTextColor(getColor(R.color.white))
    setTextSizeRes(R.dimen._7ssp)
}