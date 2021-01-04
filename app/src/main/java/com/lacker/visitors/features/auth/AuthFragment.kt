package com.lacker.visitors.features.auth

import android.content.Intent
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.lacker.utils.extensions.*
import com.lacker.visitors.R
import com.lacker.visitors.features.auth.AuthMachine.State
import com.lacker.visitors.features.auth.AuthMachine.Wish
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import kotlinx.android.synthetic.main.fragment_auth.*
import timber.log.Timber

//TODO авторизация через сервисы / войти по мылу / войти как сотрудник / создать пользователя
class AuthFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = AuthFragment()

        private const val REQUEST_CODE_SIGN_IN_GOOGLE = 50
    }

    override val machine by lazy { getMachineFromFactory(AuthMachine::class.java) }

    override fun layoutRes(): Int = R.layout.fragment_auth

    override val toolbarSettings: ToolbarFragmentSettings? = null

    private val googleApiClient by lazy {
        GoogleSignIn.getClient(
            requireContext(),
            googleSignInOptions
        )
    }

    private val googleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }

    override fun onScreenInit() {

        logoImageAuth.setOnClickListener {
            emailFieldLayout.isEnabled = !emailFieldLayout.isEnabled
            passwordFieldLayout.isEnabled = !passwordFieldLayout.isEnabled
            loginButton.isEnabled = !loginButton.isEnabled
            if (loginButton.isEnabled) requestKeyboard(true)
            else hideKeyboard()
            signIn()
        }

        requestKeyboard()

        emailField.doAfterTextChanged {
            performWish(Wish.Email(it?.toString().orEmpty()))
        }

        passwordField.doAfterTextChanged {
            performWish(Wish.Password(it?.toString().orEmpty()))
        }

        loginButton.setOnClickListener { /*performWish(Wish.SignIn)*/ }

        passwordField.setOnEditorActionListener { _, actionId, _ ->
            (actionId == EditorInfo.IME_ACTION_DONE).also {
                if (it) performWish(Wish.SignIn)
            }
        }
    }

    override fun render(state: State) {
        loginProgress.visible = state.loading

        emailFieldLayout.error = state.emailErrorText
        emailField.setTextIfNotEquals(state.email)

        passwordFieldLayout.error = state.passwordErrorText
        passwordField.setTextIfNotEquals(state.password)
    }

    override fun onResume() {
        super.onResume()
        requestKeyboard()
    }

    private fun requestKeyboard(debug: Boolean = false) { //TODO remove debug later
        if (emailField.text.isNullOrBlank() && debug) {
            emailField.requestFocus()
            showKeyboard()
        }
    }

    private fun signIn() { // TODO call by google button
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
            val account = completedTask.getResult(ApiException::class.java) ?: return
            account.id.alsoPrintDebug("id")
            account.displayName.alsoPrintDebug("displayName")
            account.email.alsoPrintDebug("email")
            account.familyName.alsoPrintDebug("familyName")
            account.givenName.alsoPrintDebug("givenName")
        } catch (e: ApiException) {
            Timber.e(e) // TODO delete this!
            //TODO some unknown exception happened workaround later
        }
    }

}