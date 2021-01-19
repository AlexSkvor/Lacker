package com.lacker.visitors.features.auth.main

import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.api.NetworkManager
import com.lacker.visitors.data.dto.auth.toDomainUser
import com.lacker.visitors.data.storage.user.UserStorage
import javax.inject.Inject
import com.lacker.visitors.features.auth.main.MainAuthMachine.Wish
import com.lacker.visitors.features.auth.main.MainAuthMachine.State
import com.lacker.visitors.features.auth.main.MainAuthMachine.Result
import com.lacker.visitors.navigation.Screens
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class MainAuthMachine @Inject constructor(
    private val router: Router,
    private val net: NetworkManager,
    private val userStorage: UserStorage
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        data class HasScreenBefore(val has: Boolean) : Wish()

        data class SignInGoogle(val data: GoogleAuthData) : Wish()

        object OpenLackerSignInScreen : Wish()
        object OpenLackerSignUpScreen : Wish()
        object OpenStaffSignInScreen : Wish()
    }

    sealed class Result {
        object SuccessSignIn : Result()
        data class SignInError(val userText: String) : Result()
    }

    data class State(
        val hasScreenBefore: Boolean = false,
        val loading: Boolean = false
    )

    override val initialState: State = State()

    override fun onBackPressed() = router.exit()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        is Wish.HasScreenBefore -> oldState.copy(hasScreenBefore = wish.has)
        is Wish.SignInGoogle -> oldState.copy(loading = true).also {
            pushResult { trySignInGoogle(wish.data) }
        }
        Wish.OpenLackerSignInScreen -> throw Exception("This is obsolete, preparing to fully deleting!")
        Wish.OpenLackerSignUpScreen -> throw Exception("This is obsolete, preparing to fully deleting!")
        Wish.OpenStaffSignInScreen -> throw Exception("This is obsolete, preparing to fully deleting!")
    }

    private suspend fun trySignInGoogle(data: GoogleAuthData): Result {
        return when (val res = net.callResult { signInWithGoogle(data) }) {
            is ApiCallResult.Result -> {
                userStorage.user = res.value.user.toDomainUser()
                Result.SuccessSignIn
            }
            is ApiCallResult.ErrorOccurred -> Result.SignInError(res.text)
        }
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        Result.SuccessSignIn -> oldState.copy(loading = false).also {
            if (it.hasScreenBefore) router.exit()
            else router.navigateTo(Screens.HomeScreen)
        }
        is Result.SignInError -> oldState.copy(loading = false).also { sendMessage(res.userText) }
    }

}