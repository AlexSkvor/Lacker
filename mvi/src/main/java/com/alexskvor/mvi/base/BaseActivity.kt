package com.alexskvor.mvi.base

import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.alexskvor.mvi.listeners.ToolbarOwner
import com.alexskvor.mvi.listeners.VolumeKeysPressListener

abstract class BaseActivity : AppCompatActivity(), ToolbarOwner {

    protected abstract val currentFragment: BaseFragment?

    override fun onBackPressed() {
        currentFragment?.onBackPressed() ?: super.onBackPressed()
    }

    override fun refreshToolbar() {
        currentFragment?.toolbarSettings?.let {
            supportActionBar?.show()
            supportActionBar?.title = it.title
            supportActionBar?.subtitle = it.subtitle
            supportActionBar?.setDisplayHomeAsUpEnabled(it.showBackIcon)
        } ?: supportActionBar?.hide()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        val volumeListener = currentFragment as? VolumeKeysPressListener

        return volumeListener?.let {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    it.onVolumeDown()
                    true
                }
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    it.onVolumeUp()
                    true
                }
                else -> super.onKeyDown(keyCode, event)
            }
        } ?: super.onKeyDown(keyCode, event)
    }
}