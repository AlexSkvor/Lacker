package com.alexskvor.utils.resources

import android.content.Context
import androidx.annotation.StringRes

class ResourceProviderImpl(private val context: Context) : ResourceProvider {

    override fun getString(@StringRes id: Int): String = context.getString(id)

    override fun getString(@StringRes id: Int, vararg args: Any): String = context.getString(id, *args)

}