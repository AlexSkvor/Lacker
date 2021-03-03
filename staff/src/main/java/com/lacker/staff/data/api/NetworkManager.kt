package com.lacker.staff.data.api

import com.lacker.staff.R
import com.lacker.utils.api.isTokenError
import com.lacker.utils.api.logError
import com.lacker.utils.extensions.onNull
import com.lacker.utils.resources.ResourceProvider
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CancellationException
import java.lang.Exception
import javax.inject.Inject

class NetworkManager @Inject constructor(
    private val json: Moshi,
    private val api: Api,
    private val resourceProvider: ResourceProvider
) {

    suspend fun <T> callResult(apiCall: suspend Api.() -> T) = try {
        ApiCallResult.Result(api.apiCall())
    } catch (e: Exception) {
        if (e is CancellationException) throw e

        val apiMessage = e.logError(json)
        if (e.isTokenError() && !loginCall()) ApiCallResult.ErrorOccurred<T>(
            resourceProvider.getString(R.string.tokenError)
        )//.also { onTokenExpired() }
        else ApiCallResult.ErrorOccurred(
            apiMessage?.message.onNull(resourceProvider.getString(R.string.unknownErrorNotification))
        )
    }

    private fun loginCall(): Boolean = TODO("rework this logic maximum priority!")
}

sealed class ApiCallResult<T> {
    data class Result<T>(val value: T) : ApiCallResult<T>()
    data class ErrorOccurred<T>(val text: String) : ApiCallResult<T>()

    fun getErrorOrNull(): String? = when (this) {
        is Result -> null
        is ErrorOccurred -> text
    }
}