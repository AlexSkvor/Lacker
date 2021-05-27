package com.lacker.staff.features.tasks.adapters

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.dto.appeal.AppealDto
import com.lacker.dto.appeal.AppealType
import com.lacker.staff.R
import com.lacker.utils.extensions.*
import kotlinx.android.synthetic.main.item_appeal.*

fun appealAdapter(
    canAccept: Boolean,
    onAccept: (AppealDto) -> Unit,
) = adapterDelegateLayoutContainer<AppealDto, Any>(R.layout.item_appeal) {

    itemAppealAcceptButton.setOnClickListener { onAccept(item) }

    bind {
        itemAppealAcceptButton.visible = canAccept

        val time = try {
            if (item.created.isToday()) item.created.format(userFormatterTimeWithoutSecs)
            else item.created.format(userFormatterSpacesWithoutSecs)
        } catch (t: Throwable) {
            null
        }

        itemAppealDateTimeTitle.text = if (time != null) getString(R.string.appealTitle, time)
        else getString(R.string.failedToParseDateTime)

        val itemSuborderClientText = getString(R.string.client, item.user.name)
        val itemSuborderClientRecolorPart = itemSuborderClientText.substringBefore(item.user.name)
        itemAppealClient.text = itemSuborderClientText
            .withBold(itemSuborderClientRecolorPart)

        val itemSuborderTableText = getString(R.string.table, item.table.title)
        val itemSuborderTableRecolorPart = itemSuborderTableText.substringBefore(item.table.title)
        itemAppealTable.text = itemSuborderTableText
            .withBold(itemSuborderTableRecolorPart)


        val appealTypeText = when (item.target) {
            AppealType.PAYMENT_BANK -> getString(R.string.typeBank)
            AppealType.PAYMENT_CASH -> getString(R.string.typeCash)
            AppealType.CONSULTATION -> getString(R.string.typeInfo)
            AppealType.UNKNOWN -> getString(R.string.typeUnknown)
        }
        val appealTypeFullText = getString(R.string.type, appealTypeText)
        val appealTypeTextRecolorPart = appealTypeFullText.substringBefore(appealTypeText)
        itemAppealType.text = appealTypeFullText.withBold(appealTypeTextRecolorPart)
    }

}