package com.lacker.visitors.features.profile

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.lacker.utils.extensions.loadDrawableRes
import com.lacker.utils.extensions.loadFromNet
import com.lacker.visitors.R
import com.lacker.visitors.data.storage.user.User
import com.lacker.visitors.di.DependencyProvider
import com.lacker.visitors.features.auth.bottomdialog.withAuthCheck
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.profile.ProfileMachine.Wish
import com.lacker.visitors.features.profile.ProfileMachine.State
import kotlinx.android.synthetic.main.fragment_profile.*
import voodoo.rocks.flux.interfaces.UserNotifier
import voodoo.rocks.flux.interfaces.getImplementation
import javax.inject.Inject

class ProfileFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_profile

    @Inject
    lateinit var googleApiClient: GoogleSignInClient

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
        DependencyProvider.get().component.inject(this)
    }

    override fun render(state: State) {
        if (state.user.isEmpty()) renderEmpty()
        else renderUser(state.user)
    }

    private fun renderEmpty() {
        avatarProfile.loadDrawableRes(R.drawable.ic_baseline_person_24)
        fullName.text = getString(R.string.guest)
        signButton.text = getString(R.string.signIn)
        signButton.setOnClickListener {
            withAuthCheck(true, R.string.authReasonFromProfile) {
                performWish(Wish.SignedIn)
            }
        }
    }

    private fun renderUser(user: User) {
        avatarProfile.loadFromNet(user.fullPhotoUrl, crossFade = false)
        fullName.text = user.name + " " + user.surname
        signButton.text = getString(R.string.signOut)
        signButton.setOnClickListener {
            googleApiClient.signOut()
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) performWish(Wish.SignedOut)
                    else getImplementation(UserNotifier::class.java)
                        ?.notify(getString(R.string.unknownErrorNotification))
                }
        }
    }
}