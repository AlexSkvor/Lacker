package com.lacker.visitors.features.session

interface SessionHolder {

    fun onSessionScreenStart(nextFragment: SessionScreen)

    fun closeNavigationAnimated()
    fun showNavigationAnimated()

}