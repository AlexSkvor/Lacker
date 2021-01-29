package com.lacker.visitors.utils

import androidx.recyclerview.widget.RecyclerView

/**
 * Calls listener with true param if scrolled up
 */
fun RecyclerView.onScroll(listener: (Boolean) -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 100) listener(false)
            if (dy < -100) listener(true)
        }
    })
}