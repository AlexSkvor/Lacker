package com.lacker.staff.features.orders.adapters

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.staff.R
import com.lacker.staff.data.dto.orders.SubOrderListItem
import com.lacker.utils.extensions.*
import kotlinx.android.synthetic.main.item_suborder.*

fun orderAdapter(
    onViewClick: (SubOrderListItem) -> Unit,
    onAcceptClick: (SubOrderListItem) -> Unit,
) = adapterDelegateLayoutContainer<SubOrderListItem, Any>(R.layout.item_suborder) {

    itemSuborderViewButton.setOnClickListener { onViewClick(item) }
    itemSuborderButton.setOnClickListener { onAcceptClick(item) }

    bind {
        val timeText = try {
            if (item.createdDateTime.isToday()) item.createdDateTime.format(
                userFormatterTimeWithoutSecs
            )
            else item.createdDateTime.format(userFormatterSpacesWithoutSecs)
        } catch (t: Throwable) {
            null
        }
        if (timeText == null) itemSuborderDateTimeTitle.text =
            getString(R.string.failedToParseDateTime)
        else itemSuborderDateTimeTitle.text = getString(R.string.ordered, timeText)

        val itemSuborderClientText = getString(R.string.client, item.clientName)
        val itemSuborderClientRecolorPart = itemSuborderClientText.substringBefore(item.clientName)
        itemSuborderClient.text = itemSuborderClientText
            .withBold(itemSuborderClientRecolorPart)

        val itemSuborderTableText = getString(R.string.table, item.tableName)
        val itemSuborderTableRecolorPart = itemSuborderTableText.substringBefore(item.tableName)
        itemSuborderTable.text = itemSuborderTableText
            .withBold(itemSuborderTableRecolorPart)

        val itemSuborderCommentText = getString(R.string.orderComment, item.comment)
        val itemSuborderCommentRecolorPart = itemSuborderCommentText.substringBefore(item.comment)
        itemSuborderComment.text = itemSuborderCommentText
            .withBold(itemSuborderCommentRecolorPart)

        val dishNumber = item.orderList.sumBy { it.portions.sumBy { p -> p.count } }
        val itemSuborderDishNumberText = getString(R.string.dishNumber, dishNumber)
        val itemSuborderDishNumberRecolorPart = itemSuborderDishNumberText
            .substringBefore(dishNumber.toString())
        itemSuborderDishNumber.text = itemSuborderDishNumberText
            .withBold(itemSuborderDishNumberRecolorPart)
    }

}