package com.lacker.visitors.features.profile

import com.lacker.visitors.data.storage.user.User
import com.lacker.visitors.data.storage.user.UserStorage
import javax.inject.Inject
import com.lacker.visitors.features.profile.ProfileMachine.Wish
import com.lacker.visitors.features.profile.ProfileMachine.State
import com.lacker.visitors.features.profile.ProfileMachine.Result
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class ProfileMachine @Inject constructor(
    private val router: Router,
    private val userStorage: UserStorage
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        object SignedOut : Wish() // TODO send request to server!
        object SignedIn : Wish()
    }

    sealed class Result

    data class State(
        val user: User
    )

    override val initialState: State = State(userStorage.user)

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        Wish.SignedOut -> {
            userStorage.user = User.empty()
            oldState.copy(user = userStorage.user)
        }
        Wish.SignedIn -> oldState.copy(user = userStorage.user)
    }

    override fun onResult(res: Result, oldState: State): State = oldState

    override fun onBackPressed() {
        router.exit()
    }
}