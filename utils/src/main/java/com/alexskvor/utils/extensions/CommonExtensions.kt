package com.alexskvor.utils.extensions

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

inline fun <reified T> T?.isNull(): Boolean = (this == null)

inline fun <reified T> T?.isNotNull(): Boolean = !isNull()

inline fun <reified T> T?.onNull(default: T): T = this ?: default

fun isMainThread() = Thread.currentThread().name == "main"
