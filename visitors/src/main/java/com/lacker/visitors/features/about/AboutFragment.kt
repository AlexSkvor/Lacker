package com.lacker.visitors.features.about

import com.lacker.visitors.R
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.visitors.features.about.AboutMachine.Wish
import com.lacker.visitors.features.about.AboutMachine.State

class AboutFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = AboutFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_about

    override val machine by lazy { getMachineFromFactory(AboutMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.aboutScreenTitle),
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