package com.lacker.visitors.features.history

import javax.inject.Inject
import com.lacker.visitors.features.history.HistoryMachine.Wish
import com.lacker.visitors.features.history.HistoryMachine.State
import com.lacker.visitors.features.history.HistoryMachine.Result
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class HistoryMachine @Inject constructor(
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