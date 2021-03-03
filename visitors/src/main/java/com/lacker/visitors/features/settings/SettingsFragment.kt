package com.lacker.visitors.features.settings

import com.lacker.visitors.R
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.settings.SettingsMachine.Wish
import com.lacker.visitors.features.settings.SettingsMachine.State

class SettingsFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_settings

    override val machine by lazy { getMachineFromFactory(SettingsMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.settingsScreenTitle),
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