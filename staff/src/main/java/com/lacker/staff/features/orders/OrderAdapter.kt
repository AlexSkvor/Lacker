package com.lacker.staff.features.orders

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.dto.order.OrderStatus
import com.lacker.dto.order.OrderWithoutSuborders
import com.lacker.staff.R
import com.lacker.utils.extensions.*
import kotlinx.android.synthetic.main.item_order.*

fun getOrderAdapter(
    onViewClick: (OrderWithoutSuborders) -> Unit,
) = adapterDelegateLayoutContainer<OrderWithoutSuborders, Any>(R.layout.item_order) {

    itemOrderViewButton.setOnClickListener { onViewClick(item) }

    bind {
        val timeText = try {
            if (item.created.isToday()) item.created.format(userFormatterTimeWithoutSecs)
            else item.created.format(userFormatterSpacesWithoutSecs)
        } catch (t: Throwable) {
            null
        }

        itemOrderDateTimeTitle.text = if (timeText != null) getString(R.string.ordered, timeText)
        else getString(R.string.failedToParseDateTime)

        val itemSuborderClientText = getString(R.string.client, item.user.name)
        val itemSuborderClientRecolorPart = itemSuborderClientText.substringBefore(item.user.name)
        itemOrderClient.text = itemSuborderClientText.withBold(itemSuborderClientRecolorPart)

        val itemSuborderTableText = getString(R.string.table, item.table.title)
        val itemSuborderTableRecolorPart = itemSuborderTableText.substringBefore(item.table.title)
        itemOrderTable.text = itemSuborderTableText.withBold(itemSuborderTableRecolorPart)

        val orderStatus = when (item.status) {
            OrderStatus.NEW -> getString(R.string.statusActive)
            OrderStatus.PAID -> getString(R.string.statusPaid)
            OrderStatus.CANCELLED -> getString(R.string.statusCanceled)
            OrderStatus.UNKNOWN -> getString(R.string.statusUnknown)
        }
        val orderStatusText = getString(R.string.status, orderStatus)
        val orderStatusRecolorPart = orderStatusText.substringBefore(orderStatus)
        itemOrderStatus.text = orderStatusText.withBold(orderStatusRecolorPart)
    }
}
