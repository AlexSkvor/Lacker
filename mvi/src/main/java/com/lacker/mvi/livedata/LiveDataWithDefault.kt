package com.lacker.mvi.livedata

import androidx.lifecycle.LiveData

open class LiveDataWithDefault<T: Any>(private val default: T) : LiveData<T>(default) {

    override fun getValue(): T {
        return super.getValue() ?: default
    }

}