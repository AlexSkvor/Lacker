package com.alexskvor.appblank.data.api

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.terrakok.cicerone.Router
import com.alexskvor.appblank.R
import com.alexskvor.appblank.data.storage.User
import com.alexskvor.appblank.data.storage.UserStorage
import com.alexskvor.appblank.navigation.Screens
import com.alexskvor.utils.extensions.onNull
import com.alexskvor.utils.resources.ResourceProvider
import java.lang.Exception
import javax.inject.Inject

class NetworkManager @Inject constructor(
    private val json: Moshi,
    private val api: Api,
    private val resourceProvider: ResourceProvider,
    private val router: Router,
    private val userStorage: UserStorage
) {

    suspend fun <T> callResult(apiCall: suspend Api.() -> T) = try {
        ApiCallResult.Result(api.apiCall())
    } catch (e: Exception) {
        if (e is CancellationException) throw e

        val apiMessage = e.logError(json)
        if (e.isTokenError() && !loginCall()) ApiCallResult.ErrorOccurred<T>(
            resourceProvider.getString(R.string.tokenError)
        ).also { onTokenExpired() }
        else ApiCallResult.ErrorOccurred(
            apiMessage?.message.onNull(resourceProvider.getString(R.string.unknownErrorNotification))
        )
    }

    private suspend fun onTokenExpired() {
        userStorage.user = User.empty()
        withContext(Dispatchers.Main) {
            launch { router.newRootScreen(Screens.AuthScreen) }
        }
    }

    private fun loginCall(): Boolean = userStorage.user.isEmpty()
}

sealed class ApiCallResult<T> {
    data class Result<T>(val value: T) : ApiCallResult<T>()
    data class ErrorOccurred<T>(val text: String) : ApiCallResult<T>()

    fun getErrorOrNull(): String? = when (this) {
        is Result -> null
        is ErrorOccurred -> text
    }
}