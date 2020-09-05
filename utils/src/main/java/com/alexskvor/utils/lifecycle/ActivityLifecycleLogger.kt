package com.alexskvor.utils.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import timber.log.Timber

object ActivityLifecycleLogger : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        (activity as? FragmentActivity)
            ?.supportFragmentManager
            ?.registerFragmentLifecycleCallbacks(FragmentLifecycleLogger, true)

        activity.logLifecycle("onCreate")
    }

    override fun onActivityStarted(activity: Activity) = activity.logLifecycle("onStart")

    override fun onActivityResumed(activity: Activity) = activity.logLifecycle("onResume")

    override fun onActivityPaused(activity: Activity) = activity.logLifecycle("onPause")

    override fun onActivityStopped(activity: Activity) = activity.logLifecycle("onStop")

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) =
        activity.logLifecycle("onSaveInstanceState")

    override fun onActivityDestroyed(activity: Activity) = activity.logLifecycle("onDestroy")

    private fun Activity.logLifecycle(methodName: String) {
        if (LoggingController.activityLifecycleLoggingEnabled)
            Timber.d("${this::class.java} $methodName")
    }
}