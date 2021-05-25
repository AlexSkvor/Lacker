package com.lacker.staff.features.auth

import com.lacker.staff.R
import com.lacker.staff.data.api.ApiCallResult
import com.lacker.staff.data.api.NetworkManager
import com.lacker.staff.data.dto.auth.AuthRequest
import com.lacker.staff.data.dto.auth.toDomain
import com.lacker.staff.data.storage.user.UserStorage
import javax.inject.Inject
import com.lacker.staff.features.auth.AuthMachine.Wish
import com.lacker.staff.features.auth.AuthMachine.State
import com.lacker.staff.features.auth.AuthMachine.Result
import com.lacker.staff.navigation.Screens
import com.lacker.utils.extensions.isValidEmail
import com.lacker.utils.extensions.onNull
import com.lacker.utils.resources.ResourceProvider
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class AuthMachine @Inject constructor(
    private val net: NetworkManager,
    private val router: Router,
    private val resourceProvider: ResourceProvider,
    private val userStorage: UserStorage,
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        object SignIn : Wish()

        data class Email(val text: String) : Wish()
        data class Password(val text: String) : Wish()
    }

    sealed class Result {
        sealed class SignIn : Result() {
            object Success : Result.SignIn()
            data class Error(val text: String) : Result.SignIn()
        }
    }

    data class State(
        val loading: Boolean = false,
        val email: String = "",
        val password: String = "",
        val errorTextEmail: String? = null,
        val errorTextPassword: String? = null
    )

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        is Wish.Email -> oldState.copy(
            email = wish.text, errorTextEmail = null
        )
        is Wish.Password -> oldState.copy(
            password = wish.text, errorTextPassword = null
        )
        Wish.SignIn -> checkStateAndSignInIfPossible(oldState)
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        Result.SignIn.Success -> oldState.also { router.replaceScreen(Screens.TasksScreen) }
        is Result.SignIn.Error -> oldState.copy(loading = false).also { sendMessage(res.text) }
    }

    private fun checkStateAndSignInIfPossible(oldState: State): State {
        if (oldState.loading) return oldState

        val emailError = when {
            oldState.email.isBlank() -> resourceProvider.getString(R.string.emailEmptyError)
            !isValidEmail(oldState.email) -> resourceProvider.getString(R.string.emailIsNotValid)
            else -> null
        }

        val passwordError = if (oldState.password.isNotBlank()) null
        else resourceProvider.getString(R.string.passwordEmptyError)

        if (emailError != null || passwordError != null)
            return oldState.copy(
                errorTextEmail = emailError,
                errorTextPassword = passwordError
            )

        pushResult { auth(oldState.email, oldState.password) }
        return oldState.copy(loading = true)
    }

    private suspend fun auth(
        email: String,
        password: String
    ): Result.SignIn {
        val request = AuthRequest(email, password)

        return when (val res = net.authCallResult { signIn(request) }) {
            is ApiCallResult.Result -> {
                userStorage.user = res.value.data.toDomain()
                Result.SignIn.Success
            }
            is ApiCallResult.ErrorOccurred -> Result.SignIn.Error(res.text)
        }
    }

    private var lastClickTime: Long? = null
    override fun onBackPressed() {
        val now = System.currentTimeMillis()
        if (lastClickTime == null || now - lastClickTime.onNull(0) > 2000) {
            lastClickTime = now
            sendMessage(resourceProvider.getString(R.string.clickAgainToCloseApp))
        } else router.exit()
    }
}