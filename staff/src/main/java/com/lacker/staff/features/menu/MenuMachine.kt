package com.lacker.staff.features.menu

import com.lacker.dto.menu.MenuItem
import com.lacker.staff.data.api.ApiCallResult
import com.lacker.staff.data.dto.orders.Dish
import com.lacker.staff.data.dto.orders.DomainPortion
import com.lacker.staff.data.storage.menu.MenuManager
import javax.inject.Inject
import com.lacker.staff.features.menu.MenuMachine.Wish
import com.lacker.staff.features.menu.MenuMachine.State
import com.lacker.staff.features.menu.MenuMachine.Result
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine
import voodoo.rocks.paginator.reduce.Ask
import voodoo.rocks.paginator.reduce.PaginationList
import voodoo.rocks.paginator.reduce.Receive
import voodoo.rocks.paginator.reduce.defaultErrorMessage

// TODO filters?
class MenuMachine @Inject constructor(
    private val router: Router,
    private val menuManager: MenuManager,
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        data class PaginationAsk(val ask: Ask) : Wish()
    }

    sealed class Result {
        data class ReceiveMenu(val receive: Receive<Dish>) : Result()
    }

    data class State(
        val menu: PaginationList<Dish> = PaginationList.EmptyProgress(),
    )

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        is Wish.PaginationAsk -> oldState.copy(menu = oldState.menu.onAsk(wish.ask) {
            pushResult { getMenu(it) }
        })
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        is Result.ReceiveMenu -> oldState.copy(menu = oldState.menu.onReceive(res.receive) {
            it.defaultErrorMessage()?.let { error -> sendMessage(error) }
        })
    }

    private suspend fun getMenu(page: Int): Result.ReceiveMenu {
        if (page > 1) return Result.ReceiveMenu(Receive.NewPage(page, emptyList()))

        val receive = when (val res = menuManager.getMenu()) {
            is ApiCallResult.Result -> Receive.NewPage(page, res.value.map { it.toDish() })
            is ApiCallResult.ErrorOccurred -> Receive.PageError(res.text)
        }
        return Result.ReceiveMenu(receive)
    }

    private fun MenuItem.toDish() = Dish(
        dishId = id,
        dishName = name,
        shortDescription = shortDescription,
        photoFullUrl = photoFullUrl,
        tags = tags,
        stopped = stopped,
        portions = portions.sortedBy { it.sort }.map {
            DomainPortion(
                id = it.id,
                price = it.price,
                portionName = it.portionName,
                count = 0
            )
        },
    )

    override fun onBackPressed() {
        router.exit()
    }
}