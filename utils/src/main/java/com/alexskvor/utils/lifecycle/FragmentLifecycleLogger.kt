package com.alexskvor.utils.lifecycle

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import timber.log.Timber

object FragmentLifecycleLogger : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        f.logLifecycle("onAttach")
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        f.logLifecycle("onCreate")
    }

    override fun onFragmentActivityCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        f.logLifecycle("onActivityCreated")
    }

    override fun onFragmentViewCreated(
        fm: FragmentManager,
        f: Fragment,
        v: View,
        savedInstanceState: Bundle?
    ) {
        f.logLifecycle("onViewCreated")
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        f.logLifecycle("onStart")
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        f.logLifecycle("onResume")
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        f.logLifecycle("onPause")
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        f.logLifecycle("onStop")
    }

    override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
        f.logLifecycle("onSveInstanceState")
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        f.logLifecycle("onDestroyView")
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        f.logLifecycle("onDestroy")
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        f.logLifecycle("onDetach")
    }

    private fun Fragment.logLifecycle(methodName: String) {
        if (LoggingController.fragmentsLifecycleLoggingEnabled)
            Timber.d("${this::class.java} $methodName")
    }

}