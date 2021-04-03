package com.lacker.staff.features.profile

import android.annotation.SuppressLint
import com.lacker.staff.R
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.staff.features.profile.ProfileMachine.Wish
import com.lacker.staff.features.profile.ProfileMachine.State
import com.lacker.utils.extensions.loadFromNet
import kotlinx.android.synthetic.main.fragment_profile.*

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
        signOutButton.setOnClickListener { performWish(Wish.SignOut) }
    }

    @SuppressLint("SetTextI18n")
    override fun render(state: State) {
        avatarProfile.loadFromNet(state.user.fullPhotoUrl, crossFade = false)
        fullName.text = state.user.name + " " + state.user.surname
    }
}