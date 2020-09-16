package com.lacker.visitors.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import com.lacker.visitors.data.dto.auth.LoginResponse
import com.lacker.visitors.data.dto.auth.UserLoginRequest

interface Api {

    @POST("account/sign-in")
    suspend fun login(
        @Body request: UserLoginRequest
    ): LoginResponse

}