package com.lacker.visitors.features.auth.main

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.lacker.utils.extensions.getArgument
import com.lacker.utils.extensions.visible
import com.lacker.utils.extensions.withArguments
import com.lacker.visitors.R
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.auth.main.MainAuthMachine.Wish
import com.lacker.visitors.features.auth.main.MainAuthMachine.State
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import kotlinx.android.synthetic.main.fragment_main_auth.*
import timber.log.Timber

class MainAuthFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance(hasScreensBefore: Boolean) = MainAuthFragment()
            .withArguments(HAS_SCREENS_BEFORE_KEY to hasScreensBefore)

        private const val HAS_SCREENS_BEFORE_KEY = "MainAuthFragment HAS_SCREENS_BEFORE_KEY"

        private const val REQUEST_CODE_SIGN_IN_GOOGLE = 50
    }

    private val hasScreensBefore: Boolean by lazy { getArgument(HAS_SCREENS_BEFORE_KEY) }

    override val toolbarSettings: ToolbarFragmentSettings? = null

    override val machine by lazy { getMachineFromFactory(MainAuthMachine::class.java) }

    override fun layoutRes(): Int = R.layout.fragment_main_auth

    override fun onScreenInit() {
        //TODO animations?
        performWish(Wish.HasScreenBefore(hasScreensBefore))
        googleSignIn.setOnClickListener { getGoogleAccount() }
        loginLackerButton.wishOnClick { Wish.OpenLackerSignInScreen }
        createLackerAccountButton.wishOnClick { Wish.OpenLackerSignUpScreen }
        staffSignIn.wishOnClick { Wish.OpenStaffSignInScreen }
    }

    override fun render(state: State) {
        mainAuthProgress.visible = state.loading
    }


    private val googleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }

    private val googleApiClient by lazy {
        GoogleSignIn.getClient(
            requireContext(),
            googleSignInOptions
        )
    }

    private fun getGoogleAccount() {
        val signInIntent: Intent = googleApiClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN_GOOGLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = requireNotNull(completedTask.getResult(ApiException::class.java))

            val data = GoogleAuthData(
                googleId = requireNotNull(account.id),
                name = requireNotNull(account.givenName),
                surname = requireNotNull(account.familyName),
                email = requireNotNull(account.email)
            )

            performWish(Wish.SignInGoogle(data))
        } catch (e: ApiException) {
            Timber.e(e)
            // TODO show message for user!
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
            // TODO show message for user (some empty fields)!
        }
    }
}