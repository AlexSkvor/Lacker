package com.lacker.visitors.features.session.order

import javax.inject.Inject
import com.lacker.visitors.features.session.order.OrderMachine.Wish
import com.lacker.visitors.features.session.order.OrderMachine.State
import com.lacker.visitors.features.session.order.OrderMachine.Result
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class OrderMachine @Inject constructor(
    private val router: Router
) : Machine<Wish, Result, State>() {

    sealed class Wish {

    }

    sealed class Result {

    }

    data class State(
        val progress: Boolean = false
    )

    override val initialState: State = State()

    override fun onResult(res: Result, oldState: State): State = oldState

    override fun onWish(wish: Wish, oldState: State): State = oldState

    override fun onBackPressed() {
        router.exit()
    }
}