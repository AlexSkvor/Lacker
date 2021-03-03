package com.lacker.visitors.features.session.common

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import com.lacker.utils.extensions.userFormatterSpacesWithoutSecs
import com.lacker.visitors.R
import kotlinx.android.synthetic.main.item_date_time_title.*

fun getSubOrderTitleAdapter(
) = adapterDelegateLayoutContainer<SubOrderTitle, MenuAdapterItem>(R.layout.item_date_time_title) {

    bind {

        commentText.text = if (item.comment.isBlank()) getString(R.string.noComment)
        else getString(R.string.yourComment, item.comment)

        val drinksText = if (item.drinksImmediately) getString(R.string.yes)
        else getString(R.string.no)
        drinksFlagText.text = getString(R.string.drinksImmediately, drinksText)

        val timeText = try {
            item.dateTime?.format(userFormatterSpacesWithoutSecs)
        } catch (t: Throwable) {
            null
        }
        if (timeText == null) dateTimeTitle.text = getString(R.string.failedToParseDateTime)
        else dateTimeTitle.text = getString(R.string.dateTimeTitleText, timeText)
    }
}