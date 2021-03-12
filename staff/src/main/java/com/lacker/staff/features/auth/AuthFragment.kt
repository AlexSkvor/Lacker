package com.lacker.staff.features.auth

import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import com.lacker.staff.R
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.staff.features.auth.AuthMachine.Wish
import com.lacker.staff.features.auth.AuthMachine.State
import com.lacker.utils.extensions.hideKeyboard
import com.lacker.utils.extensions.setTextIfNotEquals
import com.lacker.utils.extensions.visible
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = AuthFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_auth

    override val machine by lazy { getMachineFromFactory(AuthMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings? = null

    override fun onScreenInit() {
        performWish(Wish.Start)
        restaurantView.setOnClickListener {
            val restaurants = machine.states().value.restaurants.orEmpty()
            if (restaurants.isEmpty()) performWish(Wish.Start)
            else SelectRestaurantBottomFragment.show(requireActivity(),
                restaurants = restaurants,
                listener = { performWish(Wish.Restaurant(it)) } // TODO prevent reselecting and input changes while progress!
            //TODO investigate leaks
            )
        }

        emailField.doAfterTextChanged {
            performWish(Wish.Email(it?.toString().orEmpty()))
        }

        passwordField.doAfterTextChanged {
            performWish(Wish.Password(it?.toString().orEmpty()))
        }

        loginButton.setOnClickListener { signIn() }

        passwordField.setOnEditorActionListener { _, actionId, _ ->
            (actionId == EditorInfo.IME_ACTION_DONE).also {
                if (it) signIn()
            }
        }

    }

    private fun signIn() {
        hideKeyboard()
        performWish(Wish.SignIn)
    }

    override fun render(state: State) {
        loginProgress.visible = state.loading
        loginButton.visible = !state.loading

        emailFieldLayout.error = state.errorTextEmail
        emailField.setTextIfNotEquals(state.email)

        passwordFieldLayout.error = state.errorTextPassword
        passwordField.setTextIfNotEquals(state.password)

        restaurantView.restaurant = state.selectedRestaurant
        restaurantView.active = !state.restaurantsLoading
        restaurantProgress.visible = state.restaurantsLoading
    }
}