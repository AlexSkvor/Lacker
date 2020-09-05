package com.alexskvor.appblank.features.auth

import ru.terrakok.cicerone.Router
import com.alexskvor.appblank.R
import com.alexskvor.appblank.data.api.ApiCallResult
import com.alexskvor.appblank.data.api.NetworkManager
import com.alexskvor.appblank.data.dto.auth.UserLoginRequest
import com.alexskvor.appblank.data.dto.auth.toDomainUser
import com.alexskvor.appblank.data.storage.UserStorage
import com.alexskvor.mvi.mvi.Machine
import com.alexskvor.appblank.features.auth.AuthMachine.State
import com.alexskvor.appblank.features.auth.AuthMachine.Wish
import com.alexskvor.appblank.features.auth.AuthMachine.Result
import com.alexskvor.appblank.navigation.Screens
import com.alexskvor.utils.extensions.isValidEmail
import com.alexskvor.utils.resources.ResourceProvider
import javax.inject.Inject

class AuthMachine @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val router: Router,
    private val net: NetworkManager,
    private val userStorage: UserStorage
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        data class Password(val text: String) : Wish()
        data class Email(val text: String) : Wish()

        object SignIn : Wish()
    }

    data class State(
        val password: String = "",
        val email: String = "",
        val emailErrorText: String? = null,
        val passwordErrorText: String? = null,
        val loading: Boolean = false
    )

    sealed class Result {
        data class NewPassword(val newPassword: String) : Result()
        data class NewEmail(val newEmail: String) : Result()
        data class EmailError(val errorText: String) : Result()
        data class OtherError(val errorText: String) : Result()

        object WaitingStarted : Result()
        object Success : Result()
    }

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State) = when (wish) {
        is Wish.Password -> pushResult(Result.NewPassword(wish.text))
        is Wish.Email -> pushResult(Result.NewEmail(wish.text))
        Wish.SignIn -> {
            pushResult(Result.WaitingStarted)
            pushResult { tryLogin(oldState.email.trim(), oldState.password) }
        }
    }

    private suspend fun tryLogin(email: String, password: String): Result {
        if (!isValidEmail(email)) return Result.EmailError(resourceProvider.getString(R.string.invalidEmail))

        val request = UserLoginRequest(email = email, password = password)

        return when (val res = net.callResult { login(request) }) {
            is ApiCallResult.Result -> {
                userStorage.user = res.value.user.toDomainUser()
                Result.Success
            }
            is ApiCallResult.ErrorOccurred -> Result.OtherError(res.text)
        }
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        is Result.NewPassword -> oldState.copy(password = res.newPassword, passwordErrorText = null)
        is Result.NewEmail -> oldState.copy(email = res.newEmail, emailErrorText = null)
        is Result.EmailError -> oldState.copy(loading = false).copy(emailErrorText = res.errorText)
        is Result.OtherError -> oldState.copy(loading = false).also { sendMessage(res.errorText) }
        Result.Success -> oldState.copy(loading = false)
            .also { router.replaceScreen(Screens.HomeScreen) }
        Result.WaitingStarted -> oldState.copy(loading = true)
    }

    override fun onBackPressed() = router.finishChain()
}