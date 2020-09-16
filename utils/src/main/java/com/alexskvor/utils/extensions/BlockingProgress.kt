package com.alexskvor.utils.extensions

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.alexskvor.utils.R

private const val DIALOG_TAG = "dialog fragment progress"

/**
 * Marker interface that is used for auto-removing blocking progress dialog
 * from screens that are not supposed to manipulate it
 *
 * Usage requires [voodoo.rocks.utils.lifecycle.FragmentLifecycleBlockingProgress]
 * to be set as FragmentLifecycleCallback
 */
@Deprecated("Use only if you need to call showBlockingProgress")
interface BlockingProgressOwner

@Deprecated("Prefer to use other way of user notifing. If used in render, should be placed ONLY at the end")
fun BlockingProgressOwner.showBlockingProgress(show: Boolean) {
    (this as? Fragment)?.showBlockingProgressInternal(show)
}

@Deprecated("Prefer to use other way of user notifing. If used in render, should be placed ONLY at the end")
internal fun Fragment.showBlockingProgressInternal(show: Boolean) {
    Handler(Looper.getMainLooper()).post {
        if (isResumed) activity?.showBlockingProgress(show)
    }
}

@Deprecated("Prefer to use other way of user notifing. If used in render, should be placed ONLY at the end")
private fun FragmentActivity.showBlockingProgress(visible: Boolean) {
    val fragment = supportFragmentManager.findFragmentByTag(DIALOG_TAG)
    if (fragment != null && !visible) {
        (fragment as ProgressDialogFragment).dismissAllowingStateLoss()
        supportFragmentManager.executePendingTransactions()
    } else if (fragment == null && visible) {
        val progressDialogFragment = ProgressDialogFragment()
        progressDialogFragment.show(supportFragmentManager, DIALOG_TAG)
        supportFragmentManager.executePendingTransactions()
    }
}

internal class ProgressDialogFragment : AppCompatDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(STYLE_NO_FRAME, R.style.ProgressDialogTheme)
        isCancelable = false
        return inflater.inflate(R.layout.dialog_progress, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext(), R.style.ProgressDialogTheme)
            .setView(R.layout.dialog_progress)
            .create()
    }
}