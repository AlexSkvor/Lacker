package com.alexskvor.appblank.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import com.alexskvor.appblank.data.dto.auth.LoginResponse
import com.alexskvor.appblank.data.dto.auth.UserLoginRequest

interface Api {

    @POST("account/sign-in")
    suspend fun login(
        @Body request: UserLoginRequest
    ): LoginResponse

}