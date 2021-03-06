package com.lacker.utils.base

import androidx.annotation.MenuRes

data class ToolbarFragmentSettings(
    val title: String?,
    val subtitle: String? = null,
    val showBackIcon: Boolean = true,
    @MenuRes val menuResId: Int? = null
)