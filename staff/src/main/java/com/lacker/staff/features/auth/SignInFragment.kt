package com.lacker.staff.features.auth

import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.staff.features.auth.SignInMachine.Wish
import com.lacker.staff.features.auth.SignInMachine.State

class SignInFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = SignInFragment()
    }

    override fun layoutRes(): Int = TODO("Not yet implemented!")

    override val machine by lazy { getMachineFromFactory(SignInMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = null,
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