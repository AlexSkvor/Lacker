package com.alexskvor.utils.lifecycle

import com.alexskvor.utils.BuildConfig

object LoggingController {

    var activityLifecycleLoggingEnabled: Boolean = BuildConfig.DEBUG_MODE

    var fragmentsLifecycleLoggingEnabled: Boolean = BuildConfig.DEBUG_MODE

    var alsoPrintDebugLoggingEnabled: Boolean = BuildConfig.DEBUG_MODE


}