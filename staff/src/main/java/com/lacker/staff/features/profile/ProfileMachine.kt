package com.lacker.staff.features.profile

import com.lacker.staff.data.storage.user.User
import com.lacker.staff.data.storage.user.UserStorage
import javax.inject.Inject
import com.lacker.staff.features.profile.ProfileMachine.Wish
import com.lacker.staff.features.profile.ProfileMachine.State
import com.lacker.staff.features.profile.ProfileMachine.Result
import com.lacker.staff.navigation.Screens
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class ProfileMachine @Inject constructor(
    private val router: Router,
    private val userStorage: UserStorage
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        object SignOut : Wish()
    }

    sealed class Result

    data class State(
        val user: User
    )

    override val initialState: State = State(userStorage.user)

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        Wish.SignOut -> {
            userStorage.user = User.empty()
            router.newRootScreen(Screens.SignInScreen)
            oldState
        }
    }

    override fun onResult(res: Result, oldState: State): State = oldState

    override fun onBackPressed() {
        router.exit()
    }
}