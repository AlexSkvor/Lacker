package com.lacker.utils.extensions

import android.view.View

var View.visible: Boolean
    get() = (visibility == View.VISIBLE)
    set(value) = if (value) visible() else gone()

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.hidden() {
    visibility = View.INVISIBLE
}