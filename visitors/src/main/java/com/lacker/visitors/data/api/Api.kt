package com.lacker.visitors.data.api

import com.lacker.visitors.data.dto.auth.LoginResponse
import com.lacker.visitors.data.dto.common.DateTimeResponse
import com.lacker.visitors.data.dto.menu.Menu
import com.lacker.visitors.data.dto.auth.GoogleAuthData
import com.lacker.visitors.data.dto.order.CurrentOrderResponse
import com.lacker.visitors.data.dto.order.SubOrder
import retrofit2.http.*

interface Api {

    @POST("public/auth/google")
    suspend fun signInWithGoogle(
        @Body request: GoogleAuthData
    ): LoginResponse

    @GET("restaurants/{restaurantId}/menu")
    suspend fun getRestaurantMenu(
        @Path("restaurantId") restaurantId: String
    ): Menu

    @GET("restaurants/{restaurantId}/menu-timestamp")
    suspend fun getRestaurantMenuTimestamp(
        @Path("restaurantId") restaurantId: String
    ): DateTimeResponse

    @GET("restaurants/{restaurantId}/hasTable")
    suspend fun checkRestaurantExistsAndHasTable(
        @Path("restaurantId") restaurantId: String,
        @Query("tableId") tableId: String
    )

    // TODO also use auth token later
    @POST("restaurants/{restaurantId}/callStaff/{type}")
    suspend fun callStaff(
        @Path("restaurantId") restaurantId: String,
        @Query("type") type: String,
        @Query("tableId") tableId: String
    )

    // TODO also use auth token later
    @GET("restaurants/{restaurantId}/orders/current")
    suspend fun getCurrentOrder(
        @Path("restaurantId") restaurantId: String,
        @Query("tableId") tableId: String
    ): CurrentOrderResponse

    // TODO also use auth token later
    @POST("restaurants/{restaurantId}/orders/current")
    suspend fun addToCurrentOrder(
        @Path("restaurantId") restaurantId: String,
        @Query("tableId") tableId: String,
        @Body subOrder: SubOrder
    ): CurrentOrderResponse
}