package com.alexskvor.appblank.data.api

import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import com.alexskvor.utils.BuildConfig

private const val TAG = "API_LOG... %s"

class ApiLogger(private val json: Moshi) : HttpLoggingInterceptor.Logger {

    override fun log(message: String) {
        val log = if (message.isEmpty() || message.first() !in "{[") message
        else try {
            "\n" + message.format(json)
        } catch (t: Throwable) {
            message
        }

        Timber.d(TAG, log)
    }

    companion object {
        fun get(json: Moshi): Interceptor = HttpLoggingInterceptor(ApiLogger(json)).apply {
            level = if (BuildConfig.DEBUG_MODE) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
    }
}