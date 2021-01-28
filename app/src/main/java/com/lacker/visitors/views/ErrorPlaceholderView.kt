package com.lacker.visitors.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lacker.utils.extensions.visible
import com.lacker.visitors.R
import kotlinx.android.synthetic.main.view_placeholder_error.view.*

class ErrorPlaceholderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var showError: Boolean
        get() = visible
        set(value) {
            visible = value
        }

    var errorText: String? = null
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            errorMessageText.text = "$errorTextStarter\n\n$value"
            visible = value != null
        }

    var errorTextStarter: String = context.getString(R.string.defaultErrorStarter)
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            errorText?.let { errorMessageText.text = "$value\n\n$it" }
        }

    var retryButtonText: String
        get() = retryOnErrorButton.text?.toString().orEmpty()
        set(value) {
            retryOnErrorButton.text = value
        }

    var allowRetry: Boolean
        get() = retryOnErrorButton.visible
        set(value) {
            retryOnErrorButton.visible = value
        }

    init {
        inflate(context, R.layout.view_placeholder_error, this)
            .apply { retryOnErrorButton.setOnClickListener { retryListener?.invoke() } }

        val a = context.obtainStyledAttributes(attrs, R.styleable.ErrorPlaceholderView, 0, 0)

        errorTextStarter = a.getString(R.styleable.ErrorPlaceholderView_errorStarterText)
            ?: context.getString(R.string.defaultErrorStarter)
        retryButtonText = a.getString(R.styleable.ErrorPlaceholderView_retryButtonText)
            ?: context.getString(R.string.defaultButtonRetry)
        showError = a.getBoolean(R.styleable.ErrorPlaceholderView_showErrorFromStart, false)
        allowRetry = a.getBoolean(R.styleable.ErrorPlaceholderView_allowRetry, true)

        a.recycle()
    }

    private var retryListener: (() -> Unit)? = null

    fun onRetry(listener: () -> Unit) {
        retryListener = null
    }

}