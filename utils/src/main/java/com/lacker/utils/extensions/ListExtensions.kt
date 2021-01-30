package com.lacker.utils.extensions

inline fun <reified T> List<T>.toCountMap(): Map<T, Int> {
    val map = mutableMapOf<T, Int>()
    forEach { map[it] = map[it]?.plus(1) ?: 1 }
    return map
}