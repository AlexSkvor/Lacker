package com.lacker.utils.api

import com.lacker.utils.BuildConfig
import com.lacker.utils.extensions.formatJson
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

private const val TAG = "API_LOG... %s"

class ApiLogger : HttpLoggingInterceptor.Logger {

    override fun log(message: String) {
        val log = if (message.isEmpty() || message.first() !in "{[") message
        else try {
            "\n" + message.formatJson()
        } catch (t: Throwable) {
            message
        }

        Timber.d(TAG, log)
    }

    companion object {
        fun get(): Interceptor = HttpLoggingInterceptor(ApiLogger()).apply {
            level = if (BuildConfig.DEBUG_MODE) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
    }
}