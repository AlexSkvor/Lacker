package com.lacker.utils.base

import android.view.View
import com.lacker.utils.extensions.getImplementation
import voodoo.rocks.flux.FluxFragment

abstract class ToolbarFluxFragment<W : Any, S : Any> : FluxFragment<W, S>() {

    abstract val toolbarSettings: ToolbarFragmentSettings?

    override fun onResume() {
        super.onResume()
        refreshToolbar()
    }

    protected fun refreshToolbar() {
        getImplementation(ToolbarOwner::class.java)?.refreshToolbar()
    }

    open fun onMenuItemChosen(itemId: Int): Boolean = false

    protected fun View.wishOnClick(wish: W) = setOnClickListener { performWish(wish) }
    protected fun View.wishOnClick(wish: () -> W) = wishOnClick(wish())
}