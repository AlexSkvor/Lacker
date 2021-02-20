package com.lacker.visitors.features.profile

import com.lacker.visitors.R
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.profile.ProfileMachine.Wish
import com.lacker.visitors.features.profile.ProfileMachine.State

class ProfileFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_profile

    override val machine by lazy { getMachineFromFactory(ProfileMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.profileScreenTitle),
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