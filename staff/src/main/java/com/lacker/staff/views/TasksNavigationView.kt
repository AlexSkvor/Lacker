package com.lacker.staff.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import com.lacker.staff.R
import com.lacker.utils.extensions.getColor
import com.lacker.utils.extensions.setTextSizeRes
import com.lacker.utils.extensions.setTintColor
import com.lacker.utils.extensions.visible
import kotlinx.android.synthetic.main.view_tasks_navigation.view.*

class TasksNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val views by lazy {
        mutableListOf(
            newOrdersTab to State.NEW_ORDERS,
            newCallsTab to State.NEW_CALLS,
            oldOrdersTab to State.OLD_ORDERS,
            oldCallsTab to State.OLD_CALLS
        )
    }

    private var lastClickTime: Long = 0

    init {
        inflate(context, R.layout.view_tasks_navigation, this)

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
        NEW_ORDERS, NEW_CALLS, OLD_ORDERS, OLD_CALLS
    }

    fun setNewOrdersBadge(value: Int) {
        newOrdersBadge.visible = value > 0
        newOrdersBadge.text = value.toString()
    }

    fun setNewCallsBadge(value: Int) {
        newCallsBadge.visible = value > 0
        newCallsBadge.text = value.toString()
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