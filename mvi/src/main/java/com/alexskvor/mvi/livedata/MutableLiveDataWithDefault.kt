package com.alexskvor.mvi.livedata

class MutableLiveDataWithDefault<T: Any>(default: T) : LiveDataWithDefault<T>(default) {

    public override fun postValue(value: T) {
        super.postValue(value)
    }

    public override fun setValue(value: T) {
        super.setValue(value)
    }
}