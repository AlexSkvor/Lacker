package com.lacker.staff.features.orders

import com.lacker.staff.R
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.staff.features.orders.TasksMachine.Wish
import com.lacker.staff.features.orders.TasksMachine.State

class TasksFragment : ToolbarFluxFragment<Wish, State>() {
    companion object {
        fun newInstance() = TasksFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_tasks

    override val machine by lazy { getMachineFromFactory(TasksMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.tasksScreenTitle),
            subtitle = null,
            showBackIcon = false,
            menuResId = null
        )
    }

    override fun onScreenInit() {
        //TODO("Not yet implemented")
    }

    override fun render(state: State) {
        //TODO("Not yet implemented")
    }
}