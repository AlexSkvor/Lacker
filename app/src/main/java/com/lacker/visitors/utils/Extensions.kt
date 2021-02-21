package com.lacker.visitors.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Calls listener with true param if scrolled up
 */
@SuppressLint("ClickableViewAccessibility")
fun RecyclerView.onScroll(listener: (Boolean) -> Unit) {
    var yStart: Float? = null

    setOnTouchListener { _, event ->
        if (event == null) return@setOnTouchListener false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> yStart = event.y
            MotionEvent.ACTION_MOVE -> {
                if (yStart == null) {
                    yStart = event.y
                    return@setOnTouchListener false
                }

                val dist = event.y - (yStart ?: event.y)
                val distEnough = kotlin.math.abs(dist) > 100

                if (distEnough) {
                    yStart = null
                    listener(dist > 0)
                } else return@setOnTouchListener false
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                yStart = null
            }
        }

        false
    }
}

private val formatMoney by lazy {
    val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH).apply {
        decimalSeparator = ' '
        groupingSeparator = ' '
    }
    java.text.DecimalFormat("#,###", formatSymbols)
}

fun Int.asMoney(): String = formatMoney.format(this)