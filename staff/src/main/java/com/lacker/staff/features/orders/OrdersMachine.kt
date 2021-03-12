package com.lacker.staff.features.orders

import com.lacker.staff.R
import javax.inject.Inject
import com.lacker.staff.features.orders.OrdersMachine.Wish
import com.lacker.staff.features.orders.OrdersMachine.State
import com.lacker.staff.features.orders.OrdersMachine.Result
import com.lacker.utils.extensions.onNull
import com.lacker.utils.resources.ResourceProvider
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class OrdersMachine @Inject constructor(
    private val resourceProvider: ResourceProvider,
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

    private var lastClickTime: Long? = null
    override fun onBackPressed() {
        val now = System.currentTimeMillis()
        if (lastClickTime == null || now - lastClickTime.onNull(0) > 2000) {
            lastClickTime = now
            sendMessage(resourceProvider.getString(R.string.clickAgainToCloseApp))
        } else router.exit()
    }
}