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
        val suborders = when (val res = getNewOrOldSuborders(new)) {
            is ApiCallResult.Result -> res.value.data
            is ApiCallResult.ErrorOccurred -> return ApiCallResult.ErrorOccurred(res.text)
        }

        return when (val res = mapSuborders(suborders)) {
            is ApiCallResult.Result -> ApiCallResult.Result(
                if (new) res.value.sortedBy { it.createdDateTime }
                else res.value.sortedByDescending { it.createdDateTime }
            )
            is ApiCallResult.ErrorOccurred -> return ApiCallResult.ErrorOccurred(res.text)
        }
    }

    private val restaurantId = userStorage.user.restaurantId

    private suspend fun getNewOrOldSuborders(new: Boolean) =
        if (new) net.callResult { getNewOrders(restaurantId) }
        else net.callResult { getOldOrders(restaurantId) }

    suspend fun mapSuborders(suborders: List<SubOrder>): ApiCallResult<List<SubOrderListItem>> {
        return when (val res = menuManager.getMenu()) {
            is ApiCallResult.Result -> ApiCallResult.Result(suborders.map { it.toDomain(res.value) })
            is ApiCallResult.ErrorOccurred -> ApiCallResult.ErrorOccurred(res.text)
        }
    }

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
            }.map { it.toDomain(this) },
            fullOrderId = fullOrder.id,
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