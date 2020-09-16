package com.lacker.visitors.di

class DependencyProvider private constructor() {

    lateinit var component: AppComponent

    companion object {
        private var instance: DependencyProvider? = null
        fun get(): DependencyProvider {
            return instance ?: synchronized(DependencyProvider::class.java) {
                instance ?: DependencyProvider().also { instance = it }
            }
        }
    }

}