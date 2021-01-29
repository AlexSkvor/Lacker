package com.lacker.visitors.features.session.favourite

import javax.inject.Inject
import com.lacker.visitors.features.session.favourite.FavouriteMachine.Wish
import com.lacker.visitors.features.session.favourite.FavouriteMachine.State
import com.lacker.visitors.features.session.favourite.FavouriteMachine.Result
import com.lacker.visitors.navigation.Screens
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class FavouriteMachine @Inject constructor(
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
        router.backTo(Screens.MenuScreen)
    }

}