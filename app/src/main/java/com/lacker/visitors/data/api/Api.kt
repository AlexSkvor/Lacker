package com.lacker.visitors.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import com.lacker.visitors.data.dto.auth.LoginResponse
import com.lacker.visitors.data.dto.auth.UserLoginRequest
import com.lacker.visitors.features.auth.main.GoogleAuthData

interface Api {

    @POST("account/sign-in/lacker")
    suspend fun signInWithLackerAccount(
        @Body request: UserLoginRequest
    ): LoginResponse

    @POST("account/sign-in/google")
    suspend fun signInWithGoogle(
        @Body request: GoogleAuthData
    ): LoginResponse

}