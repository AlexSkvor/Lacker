package com.lacker.utils.lifecycle

import com.lacker.utils.BuildConfig

object LoggingController {

    var activityLifecycleLoggingEnabled: Boolean = BuildConfig.DEBUG_MODE

    var fragmentsLifecycleLoggingEnabled: Boolean = BuildConfig.DEBUG_MODE

    var alsoPrintDebugLoggingEnabled: Boolean = BuildConfig.DEBUG_MODE


}