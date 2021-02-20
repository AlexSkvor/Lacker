package com.lacker.visitors.features.profile

import javax.inject.Inject
import com.lacker.visitors.features.profile.ProfileMachine.Wish
import com.lacker.visitors.features.profile.ProfileMachine.State
import com.lacker.visitors.features.profile.ProfileMachine.Result
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class ProfileMachine @Inject constructor(
    private val router: Router
) : Machine<Wish, Result, State>() {

    sealed class Wish {

    }

    sealed class Result {

    }

    data class State(
        val loading: Boolean = false
    )

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = oldState

    override fun onResult(res: Result, oldState: State): State = oldState

    override fun onBackPressed() {
        router.exit()
    }
}