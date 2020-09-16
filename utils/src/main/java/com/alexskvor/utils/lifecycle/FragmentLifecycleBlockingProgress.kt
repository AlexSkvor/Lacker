package com.alexskvor.utils.lifecycle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.alexskvor.utils.extensions.BlockingProgressOwner
import com.alexskvor.utils.extensions.ProgressDialogFragment
import com.alexskvor.utils.extensions.showBlockingProgressInternal

object FragmentLifecycleBlockingProgress : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        super.onFragmentResumed(fm, f)
        if (f !is BlockingProgressOwner && f !is ProgressDialogFragment)
            f.showBlockingProgressInternal(false)
    }

}