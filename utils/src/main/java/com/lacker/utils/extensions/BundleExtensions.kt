package com.lacker.utils.extensions

import android.os.Bundle
import androidx.fragment.app.Fragment
import java.io.Serializable
import kotlin.reflect.full.isSubclassOf

inline fun <reified T : Any> Bundle.withArgument(key: String, value: T?): Bundle {
    return if (value == null) this
    else when (value) {
        is String -> this.also { putString(key, value) }
        is Int -> this.also { putInt(key, value) }
        is Long -> this.also { putLong(key, value) }
        is Boolean -> this.also { putBoolean(key, value) }
        is Serializable -> this.also { putSerializable(key, value) }
        else -> throw IllegalArgumentException("Wrong value typeChosen! ${value::class}")
    }
}

inline fun <reified T : Any> Bundle.getArgumentOrNull(key: String): T? {
    return when (T::class) {
        String::class -> this.getString(key) as? T
        Int::class -> this.getInt(key) as? T
        Long::class -> this.getLong(key) as? T
        Boolean::class -> this.getBoolean(key) as? T
        else -> {
            if (T::class.isSubclassOf(Serializable::class)) getSerializable(key) as? T
            else throw IllegalArgumentException("Wrong value typeChosen! ${T::class}")
        }
    }
}

inline fun <reified T : Any> Fragment.getArgumentOrNull(key: String): T? =
    arguments?.getArgumentOrNull(key)

inline fun <reified T : Any> Fragment.getArgument(key: String): T =
    requireNotNull(getArgumentOrNull(key)) {
        "Argument with key $key was not set for ${Fragment::class} instance or you set wrong type!"
    }

fun Fragment.withArguments(vararg args: Pair<String, Any>) = apply {
    if (arguments == null) arguments = Bundle()
    args.forEach {
        when (it.second) {
            is String, is Long, is Int, is Boolean, is Serializable ->
                arguments?.withArgument(it.first, it.second)
            else -> throw IllegalArgumentException("Wrong value typeChosen! ${it.second::class}")
        }
    }
}