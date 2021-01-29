package com.lacker.visitors.features.session.basket

import javax.inject.Inject
import com.lacker.visitors.features.session.basket.BasketMachine.Wish
import com.lacker.visitors.features.session.basket.BasketMachine.State
import com.lacker.visitors.features.session.basket.BasketMachine.Result
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class BasketMachine @Inject constructor(
    private val router: Router
) : Machine<Wish, Result, State>(){

    sealed class Wish{

    }

    sealed class Result{

    }

    data class State(
        val menuProgress: Boolean = false
    )

    override val initialState: State = State()

    override fun onResult(res: Result, oldState: State): State = oldState

    override fun onWish(wish: Wish, oldState: State): State = oldState

    override fun onBackPressed() {
        router.exit()
    }
}