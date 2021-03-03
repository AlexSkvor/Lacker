package com.lacker.visitors.features.news

import javax.inject.Inject
import com.lacker.visitors.features.news.NewsMachine.Wish
import com.lacker.visitors.features.news.NewsMachine.State
import com.lacker.visitors.features.news.NewsMachine.Result
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class NewsMachine @Inject constructor(
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