package com.lacker.utils.api.auth

import okhttp3.Interceptor
import okhttp3.Response

class AuthHeaderInterceptor constructor(
    private val tokenProvider: TokenProvider,
) : Interceptor {

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val MARKER_AUTH_HEADER_NAME = "Fake-Auth"
        const val REQUIRES_AUTH = "$MARKER_AUTH_HEADER_NAME: null"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        if (request.header(MARKER_AUTH_HEADER_NAME) != null) {
            val token = tokenProvider.getAuthToken()
            builder.removeHeader(MARKER_AUTH_HEADER_NAME)
            token?.let {
                builder.addHeader(AUTHORIZATION, "$token")
            }
        }
        val newRequest = builder.build()
        return chain.proceed(newRequest)
    }

}