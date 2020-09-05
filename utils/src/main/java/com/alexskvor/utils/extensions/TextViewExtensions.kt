package com.alexskvor.utils.extensions

import android.widget.TextView

fun TextView.setTextIfNotEquals(newText: String) {
    if (text?.toString().orEmpty() != newText) text = newText
}