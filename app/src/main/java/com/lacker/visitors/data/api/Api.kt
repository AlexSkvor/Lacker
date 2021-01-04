package com.lacker.visitors.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import com.lacker.visitors.data.dto.auth.LoginResponse
import com.lacker.visitors.data.dto.auth.UserLoginRequest

interface Api {

    @POST("account/sign-in/lacker")
    suspend fun signInWithLackerAccount(
        @Body request: UserLoginRequest
    ): LoginResponse

}