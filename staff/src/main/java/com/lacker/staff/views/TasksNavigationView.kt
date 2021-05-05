package com.lacker.staff.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import com.lacker.staff.R
import com.lacker.staff.features.orders.TasksMachine
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
        views.forEachIndexed { i, it ->
            if (it.second == state) it.first.select()
            else it.first.unSelect(i > 1)
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

private val mapStates = mapOf(
    TasksNavigationView.State.NEW_ORDERS to TasksMachine.State.Type.NEW_ORDERS,
    TasksNavigationView.State.NEW_CALLS to TasksMachine.State.Type.NEW_CALLS,
    TasksNavigationView.State.OLD_ORDERS to TasksMachine.State.Type.OLD_ORDERS,
    TasksNavigationView.State.OLD_CALLS to TasksMachine.State.Type.OLD_CALLS,
)

fun TasksNavigationView.State.asDomain() = mapStates[this]!!

fun TasksMachine.State.Type.asUi() = mapStates.entries.firstOrNull {
    it.value == this
}?.key!!

private fun TextView.select() {
    setTintColor(R.color.brownExtraLight)
    setTextColor(getColor(R.color.brownExtraLight))
    setTextSizeRes(R.dimen._8ssp)
}

private fun TextView.unSelect(old: Boolean) {
    val colorRes = if (old) R.color.brownExtraLightWithGrey
    else R.color.white
    setTintColor(colorRes)
    setTextColor(getColor(colorRes))
    setTextSizeRes(R.dimen._7ssp)
}