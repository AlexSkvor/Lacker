package com.lacker.staff.data.storage.menu

import com.lacker.dto.order.SubOrder
import com.lacker.staff.data.dto.orders.SubOrderListItem
import javax.inject.Inject

class SubOrderMapper @Inject constructor(
    private val menuManager: MenuManager,
) {

    suspend fun map(list: List<SubOrder>): List<SubOrderListItem> = TODO()

}