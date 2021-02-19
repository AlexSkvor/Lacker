package com.lacker.visitors.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lacker.visitors.R
import kotlinx.android.synthetic.main.view_placeholder_empty.view.*

class EmptyPlaceholderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var emptyThingText: String = context.getString(R.string.defaultEmptyThing)
        set(value) {
            field = value
            emptyMessageText.text = context.getString(R.string.emptyList, value)
        }

    init {
        inflate(context, R.layout.view_placeholder_empty, this)
        val a = context.obtainStyledAttributes(attrs, R.styleable.EmptyPlaceholderView, 0, 0)

        emptyThingText = a.getString(R.styleable.EmptyPlaceholderView_emptyThing)
            ?: context.getString(R.string.defaultEmptyThing)

        a.recycle()
    }

}