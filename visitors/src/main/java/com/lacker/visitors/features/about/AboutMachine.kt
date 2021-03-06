package com.lacker.visitors.features.about

import javax.inject.Inject
import com.lacker.visitors.features.about.AboutMachine.Wish
import com.lacker.visitors.features.about.AboutMachine.State
import com.lacker.visitors.features.about.AboutMachine.Result
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class AboutMachine @Inject constructor(
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

    override fun onResult(res: Result, oldState: State): State = oldState

    override fun onWish(wish: Wish, oldState: State): State = oldState

    override fun onBackPressed() {
        router.exit()
    }
}