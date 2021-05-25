package com.lacker.staff.data.api

import com.lacker.staff.data.dto.auth.AuthRequest
import com.lacker.staff.data.dto.auth.AuthResponse
import com.lacker.staff.data.dto.auth.UserDto
import com.lacker.staff.data.dto.orders.NewOrdersPageResponse
import com.lacker.utils.api.auth.AuthHeaderInterceptor
import retrofit2.http.*

interface Api {

    @POST("public/auth/staff")
    suspend fun signIn(
        @Body request: AuthRequest
    ): AuthResponse

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @GET("api/{restaurantId}/unchecked_suborders")
    suspend fun getNewOrders(
        @Path("restaurantId") restaurantId: String,
    ): NewOrdersPageResponse
}