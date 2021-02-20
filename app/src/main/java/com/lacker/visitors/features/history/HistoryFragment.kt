package com.lacker.visitors.features.history

import com.lacker.visitors.R
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.history.HistoryMachine.Wish
import com.lacker.visitors.features.history.HistoryMachine.State

class HistoryFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_history

    override val machine by lazy { getMachineFromFactory(HistoryMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.historyScreenTitle),
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