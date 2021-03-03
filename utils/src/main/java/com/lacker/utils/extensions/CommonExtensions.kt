package com.lacker.utils.extensions

import java.text.DecimalFormatSymbols
import java.util.*

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

inline fun <reified T> T?.isNull(): Boolean = (this == null)

inline fun <reified T> T?.isNotNull(): Boolean = !isNull()

inline fun <reified T> T?.onNull(default: T): T = this ?: default

fun isMainThread() = Thread.currentThread().name == "main"

fun doNothing() = Unit

private val formatMoney by lazy {
    val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH).apply {
        decimalSeparator = ' '
        groupingSeparator = ' '
    }
    java.text.DecimalFormat("#,###", formatSymbols)
}

fun Int.asMoney(): String = formatMoney.format(this)