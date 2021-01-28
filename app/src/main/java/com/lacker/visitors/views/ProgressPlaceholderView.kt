package com.lacker.visitors.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lacker.visitors.R
import kotlinx.android.synthetic.main.view_placeholder_progress.view.*

class ProgressPlaceholderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var cookingThingText: String = context.getString(R.string.defaultProgressThing)
    set(value) {
        field = value
        progressMessageText.text = context.getString(R.string.cookingThingForYou, value)
    }

    init {
        inflate(context, R.layout.view_placeholder_progress, this)

        val a = context.obtainStyledAttributes(attrs, R.styleable.ProgressPlaceholderView, 0, 0)

        cookingThingText = a.getString(R.styleable.ProgressPlaceholderView_cookingThing)
            ?: context.getString(R.string.defaultProgressThing)

        a.recycle()
    }

}