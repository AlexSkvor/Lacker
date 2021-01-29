package com.lacker.utils.extensions

import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat

fun View.getColor(@ColorRes res: Int) = ContextCompat.getColor(context, res)

fun TextView.setTintColor(@ColorRes res: Int) {
    TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(getColor(res)))
}

fun TextView.setTextSizeRes(@DimenRes res: Int) {
    setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(res))
}