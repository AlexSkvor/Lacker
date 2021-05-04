package com.lacker.utils.extensions

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

/**
 * Calls listener with true param if scrolled up
 */
@SuppressLint("ClickableViewAccessibility")
fun View.onScroll(listener: (Boolean) -> Unit) {
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

fun List<RecyclerView>.disableBlinking() = forEach { it.disableBlinking() }

fun RecyclerView.disableBlinking() {
    (itemAnimator as? SimpleItemAnimator?)?.supportsChangeAnimations = false
}
