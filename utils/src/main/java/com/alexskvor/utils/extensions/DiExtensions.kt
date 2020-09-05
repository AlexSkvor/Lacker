package com.alexskvor.utils.extensions

import androidx.fragment.app.Fragment

fun <L : Any> Fragment.getImplementation(klass: Class<L>): L? {
    val activity = this.activity
    val parentFragment = this.parentFragment
    val targetFragment = this.targetFragment

    return when {
        klass.isInstance(activity) -> activity as L
        klass.isInstance(parentFragment) -> parentFragment as L
        klass.isInstance(targetFragment) -> targetFragment as L
        else -> parentFragment?.getImplementation(klass)
    }
}