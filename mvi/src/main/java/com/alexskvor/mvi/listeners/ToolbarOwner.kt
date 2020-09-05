package com.alexskvor.mvi.listeners

interface ToolbarOwner {

    fun refreshToolbar()

}

data class ToolbarFragmentSettings(
    val title: String?,
    val subtitle: String? = null,
    val showBackIcon: Boolean = true
)