package com.lacker.visitors.features.auth

import com.lacker.visitors.R
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.api.NetworkManager
import com.lacker.visitors.data.dto.auth.UserLoginRequest
import com.lacker.visitors.data.dto.auth.toDomainUser
import com.lacker.visitors.data.storage.user.UserStorage
import com.lacker.visitors.features.auth.AuthMachine.State
import com.lacker.visitors.features.auth.AuthMachine.Wish
import com.lacker.visitors.features.auth.AuthMachine.Result
import com.lacker.visitors.navigation.Screens
import com.lacker.utils.extensions.isValidEmail
import com.lacker.utils.resources.ResourceProvider
import com.lacker.visitors.navigation.FastClickSafeRouter
import voodoo.rocks.flux.Machine
import javax.inject.Inject

class AuthMachine @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val router: FastClickSafeRouter,
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
        data class EmailError(val errorText: String) : Result()
        data class OtherError(val errorText: String) : Result()

        object Success : Result()
    }

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        is Wish.Password -> oldState.copy(password = wish.text)
        is Wish.Email -> oldState.copy(email = wish.text)
        Wish.SignIn -> oldState.copy(loading = true).also {
            pushResult { tryLogin(oldState.email, oldState.password) }
        }
    }

    private suspend fun tryLogin(email: String, password: String): Result {
        if (!isValidEmail(email)) return Result.EmailError(resourceProvider.getString(R.string.invalidEmail))

        val request = UserLoginRequest(email = email, password = password)

        return when (val res = net.callResult { signInWithLackerAccount(request) }) {
            is ApiCallResult.Result -> {
                userStorage.user = res.value.user.toDomainUser()
                Result.Success
            }
            is ApiCallResult.ErrorOccurred -> Result.OtherError(res.text)
        }
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        is Result.EmailError -> oldState.copy(loading = false).copy(emailErrorText = res.errorText)
        is Result.OtherError -> oldState.copy(loading = false).also { sendMessage(res.errorText) }
        Result.Success -> oldState.copy(loading = false).also {
            router.replaceScreen(Screens.HomeScreen)
        }
    }

    override fun onBackPressed() = router.finishChain()
}