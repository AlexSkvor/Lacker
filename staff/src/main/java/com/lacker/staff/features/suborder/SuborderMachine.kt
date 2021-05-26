package com.lacker.staff.features.suborder

import com.lacker.staff.data.dto.orders.SubOrderListItem
import javax.inject.Inject
import com.lacker.staff.features.suborder.SuborderMachine.Wish
import com.lacker.staff.features.suborder.SuborderMachine.State
import com.lacker.staff.features.suborder.SuborderMachine.Result
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class SuborderMachine @Inject constructor(
    private val router: Router,
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        data class Suborder(val suborder: SubOrderListItem) : Wish()
    }

    sealed class Result

    data class State(
        val suborder: SubOrderListItem? = null
    )

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        is Wish.Suborder -> oldState.copy(suborder = wish.suborder)
    }

    override fun onResult(res: Result, oldState: State): State = oldState

    override fun onBackPressed() {
        router.exit()
    }
}