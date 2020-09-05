package com.alexskvor.appblank.data.api

import com.squareup.moshi.Moshi
import okio.Buffer
import retrofit2.HttpException
import timber.log.Timber
import com.alexskvor.appblank.data.dto.common.ApiException
import com.alexskvor.appblank.data.dto.common.ErrorResponse
import com.alexskvor.utils.extensions.formatJson
import java.lang.Exception
import java.nio.charset.StandardCharsets

fun Exception.isTokenError(): Boolean = (this is HttpException && response()?.code() == 401)

fun Exception.logError(json: Moshi): ApiException? {
    val userMessage = (this as? HttpException)?.logApiCallError(json)
    if (userMessage == null) Timber.e(this)
    return userMessage
}

private fun HttpException.logApiCallError(json: Moshi): ApiException? {
    val requestMethod = response()?.raw()?.request?.url?.toString().orEmpty()

    val body = response()?.raw()?.request?.body

    val buffer = Buffer()
    body?.writeTo(buffer)
    val contentType = body?.contentType()?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

    val requestBody = buffer.readString(contentType)
        .formatJson()
        .lines()
        .joinToString("\n")

    val responseBodyRaw = response()?.errorBody()?.string()

    val userMessage = responseBodyRaw?.let {
        json.adapter(ErrorResponse::class.java).fromJson(it)?.exception
    }

    val responseBody = responseBodyRaw.orEmpty().formatJson()

    val addition = "Called Api Method:\n" +
            "$requestMethod\n" +
            "Request Body:\n" +
            "$requestBody\n" +
            "Response:\n" +
            "$responseBody\n"

    val fakeException = PrettyMessageException(addition, this)
    Timber.e(fakeException)

    return userMessage
}

class PrettyMessageException(
    override val message: String,
    override val cause: Throwable?
) : Throwable(message, cause) {

    override fun fillInStackTrace(): Throwable = this

}