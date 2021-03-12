package com.lacker.utils.extensions

import android.content.res.ColorStateList
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat

fun View.getColor(@ColorRes res: Int) = ContextCompat.getColor(context, res)

fun TextView.setTintColor(@ColorRes res: Int) {
    TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(getColor(res)))
}

fun TextView.setTextSizeRes(@DimenRes res: Int) {
    setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(res))
}

fun View.drawableCompat(@DrawableRes res: Int) = ContextCompat.getDrawable(context, res)

fun View.setAllPaddings(@DimenRes res: Int) =
    setAllPaddingsPure(resources.getDimensionPixelSize(res))

fun View.setAllPaddingsPure(value: Int) = setPadding(value, value, value, value)

@RequiresApi(Build.VERSION_CODES.M)
fun View.addDefaultSelectableAnimation() {
    val outValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
    foreground = drawableCompat(outValue.resourceId)
}