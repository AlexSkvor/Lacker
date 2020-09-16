package com.lacker.utils.extensions

import android.widget.TextView

fun TextView.setTextIfNotEquals(newText: String) {
    if (text?.toString().orEmpty() != newText) text = newText
}