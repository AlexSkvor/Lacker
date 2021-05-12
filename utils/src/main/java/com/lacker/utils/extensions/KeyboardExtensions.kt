package com.lacker.utils.extensions

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard() = activity?.hideKeyboard()

fun Fragment.showKeyboard() = activity?.showKeyboard()

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = this.currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.showKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = this.currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.showSoftInput(view, 0)
}

fun Fragment.getLastTextFromClipboardOrNull() = activity?.getLastTextFromClipboardOrNull()

fun Activity.getLastTextFromClipboardOrNull(): String? {
    (getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let { clipboard ->
        clipboard.primaryClip?.let { primaryClip ->
            if (primaryClip.description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                return primaryClip.getItemAt(0)?.text?.toString()
        }
    }
    return null
}

fun Fragment.putToClipboard(text: String) = activity?.putToClipboard(text)

fun Activity.putToClipboard(text: String) {
    (getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)
        ?.setPrimaryClip(ClipData.newPlainText(text, text))
}