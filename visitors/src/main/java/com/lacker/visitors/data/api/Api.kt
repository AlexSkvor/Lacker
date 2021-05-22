package com.lacker.visitors.data.api

import com.lacker.utils.api.auth.AuthHeaderInterceptor
import com.lacker.visitors.data.dto.auth.LoginResponse
import com.lacker.visitors.data.dto.common.DateTimeResponse
import com.lacker.visitors.data.dto.auth.GoogleAuthData
import com.lacker.visitors.data.dto.menu.MenuResponse
import com.lacker.visitors.data.dto.order.AddSuborderRequest
import com.lacker.visitors.data.dto.order.CreateOrderRequest
import com.lacker.visitors.data.dto.order.CurrentOrderResponse
import com.lacker.visitors.data.dto.restaurants.TablesOfRestaurantResponse
import retrofit2.http.*

interface Api {

    @POST("public/auth/google")
    suspend fun signInWithGoogle(
        @Body request: GoogleAuthData
    ): LoginResponse

    @GET("api/{restaurantId}/main_menu")
    suspend fun getRestaurantMenu(
        @Path("restaurantId") restaurantId: String
    ): MenuResponse

    @GET("api/{restaurantId}/main_menu")
    suspend fun getRestaurantMenuTimestamp(
        @Path("restaurantId") restaurantId: String,
        @Query("fields") fields: String = "update_date"
    ): DateTimeResponse

    @GET("api/{restaurantId}/tables")
    suspend fun getTablesOfRestaurant(
        @Path("restaurantId") restaurantId: String,
    ): TablesOfRestaurantResponse

    // TODO also use auth token later
    @POST("restaurants/{restaurantId}/callStaff/{type}")
    suspend fun callStaff(
        @Path("restaurantId") restaurantId: String,
        @Query("type") type: String,
        @Query("tableId") tableId: String
    )

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @GET("api/{orderId}")
    suspend fun getOrderById(
        @Path("orderId") orderId: String,
    ): CurrentOrderResponse

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @POST("api/me/make_order")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): CurrentOrderResponse

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @POST("api/{orderId}/add_suborder")
    suspend fun addToCurrentOrder(
        @Path("orderId") orderId: String,
        @Body request: AddSuborderRequest,
    ): CurrentOrderResponse
}