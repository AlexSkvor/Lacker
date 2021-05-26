package com.lacker.staff.data.api

import com.lacker.dto.common.DateTimeResponse
import com.lacker.dto.menu.MenuResponse
import com.lacker.staff.data.dto.auth.AuthRequest
import com.lacker.staff.data.dto.auth.AuthResponse
import com.lacker.staff.data.dto.orders.SubOrdersListResponse
import com.lacker.utils.api.auth.AuthHeaderInterceptor
import retrofit2.http.*

interface Api {

    @POST("public/auth/staff")
    suspend fun signIn(
        @Body request: AuthRequest
    ): AuthResponse

    @GET("api/{restaurantId}/main_menu")
    suspend fun getRestaurantMenu(
        @Path("restaurantId") restaurantId: String
    ): MenuResponse

    @GET("api/{restaurantId}/main_menu")
    suspend fun getRestaurantMenuTimestamp(
        @Path("restaurantId") restaurantId: String,
        @Query("fields") fields: String = "update_time"
    ): DateTimeResponse

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @GET("api/{restaurantId}/unchecked_suborders")
    suspend fun getNewOrders(
        @Path("restaurantId") restaurantId: String,
    ): SubOrdersListResponse
}