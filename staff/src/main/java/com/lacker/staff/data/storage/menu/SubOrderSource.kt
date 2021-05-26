package com.lacker.staff.data.storage.menu

import com.lacker.dto.menu.MenuItem
import com.lacker.dto.menu.Portion
import com.lacker.dto.order.SubOrder
import com.lacker.staff.data.api.ApiCallResult
import com.lacker.staff.data.api.NetworkManager
import com.lacker.staff.data.dto.orders.Dish
import com.lacker.staff.data.dto.orders.DomainPortion
import com.lacker.staff.data.dto.orders.SubOrderListItem
import com.lacker.staff.data.storage.user.UserStorage
import javax.inject.Inject

class SubOrderSource @Inject constructor(
    private val menuManager: MenuManager,
    private val net: NetworkManager,
    private val userStorage: UserStorage,
) {

    suspend fun getSuborders(new: Boolean): ApiCallResult<List<SubOrderListItem>> {
        val restaurantId = userStorage.user.restaurantId
        val suborders = when (val res = getNewOrOldSuborders(new)) {
            is ApiCallResult.Result -> res.value.data
            is ApiCallResult.ErrorOccurred -> return ApiCallResult.ErrorOccurred(res.text)
        }

        val menu = when (val res = menuManager.getMenu(restaurantId)) {
            is ApiCallResult.Result -> res.value
            is ApiCallResult.ErrorOccurred -> return ApiCallResult.ErrorOccurred(res.text)
        }

        return ApiCallResult.Result(
            suborders.sortedByDescending { it.createdTimeStamp }
                .map { it.toDomain(menu) }
        )
    }

    private val restaurantId = userStorage.user.restaurantId

    private suspend fun getNewOrOldSuborders(new: Boolean) =
        if (new) net.callResult { getNewOrders(restaurantId) }
        else TODO()

    private fun SubOrder.toDomain(menu: List<MenuItem>): SubOrderListItem {
        val portionIds: List<String> = orderList.map { p -> p.portionId }
        return SubOrderListItem(
            id = id,
            clientName = clientName,
            tableName = tableName,
            createdDateTime = createdTimeStamp,
            comment = comment.orEmpty(),
            orderList = menu.filter {
                it.portions.map { p -> p.id }.any { id -> id in portionIds }
            }.map { it.toDomain(this) }
        )
    }

    private fun MenuItem.toDomain(
        order: SubOrder
    ) = Dish(
        dishId = id,
        dishName = name,
        photoFullUrl = photoFullUrl,
        shortDescription = shortDescription,
        portions = portions.sortedBy { it.sort }.map { it.toDomain(order) },
        tags = tags,
        stopped = stopped,
    )

    private fun Portion.toDomain(order: SubOrder) = DomainPortion(
        id = id,
        price = price,
        portionName = portionName,
        count = order.orderList
            .filter { it.portionId == id }
            .sumOf { it.ordered }
    )

}