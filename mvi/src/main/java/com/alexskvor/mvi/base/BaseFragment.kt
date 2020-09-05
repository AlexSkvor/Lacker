package com.alexskvor.mvi.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.alexskvor.mvi.listeners.ToolbarFragmentSettings
import com.alexskvor.mvi.listeners.ToolbarOwner
import com.alexskvor.utils.extensions.getImplementation

abstract class BaseFragment : Fragment() {

    @LayoutRes
    abstract fun layoutRes(): Int

    abstract val toolbarSettings: ToolbarFragmentSettings?

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutRes(), container, false)

    override fun onResume() {
        super.onResume()
        refreshToolbar()
    }

    protected fun refreshToolbar() {
        getImplementation(ToolbarOwner::class.java)?.refreshToolbar()
    }

    abstract fun onBackPressed()
}