package com.lacker.visitors.features.auth

import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.fragment_auth.*
import com.lacker.visitors.R
import com.lacker.mvi.mvi.MviFragment
import com.lacker.visitors.features.auth.AuthMachine.State
import com.lacker.visitors.features.auth.AuthMachine.Wish
import com.lacker.mvi.listeners.ToolbarFragmentSettings
import com.lacker.utils.extensions.setTextIfNotEquals
import com.lacker.utils.extensions.showKeyboard
import com.lacker.utils.extensions.visible

class AuthFragment : MviFragment<Wish, State>() {

    companion object {
        fun newInstance() = AuthFragment()
    }

    override val machine by lazy { getMachineFromFactory(AuthMachine::class.java) }

    override fun layoutRes(): Int = R.layout.fragment_auth

    override val toolbarSettings: ToolbarFragmentSettings? = null

    override fun onScreenInit() {

        requestKeyboard()

        emailField.doAfterTextChanged {
            performWish(Wish.Email(it?.toString().orEmpty()))
        }

        passwordField.doAfterTextChanged {
            performWish(Wish.Password(it?.toString().orEmpty()))
        }

        loginButton.setOnClickListener { performWish(Wish.SignIn) }

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

    private fun requestKeyboard() {
        if (emailField.text.isNullOrBlank()) {
            emailField.requestFocus()
            showKeyboard()
        }
    }

}