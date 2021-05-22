package com.lacker.visitors.data.api

import com.lacker.utils.api.auth.AuthHeaderInterceptor
import com.lacker.visitors.data.dto.auth.LoginResponse
import com.lacker.visitors.data.dto.common.DateTimeResponse
import com.lacker.visitors.data.dto.auth.GoogleAuthData
import com.lacker.visitors.data.dto.menu.MenuResponse
import com.lacker.visitors.data.dto.order.CurrentOrderResponse
import com.lacker.visitors.data.dto.order.SubOrder
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

    @GET("api/{restaurantId}")
    suspend fun getRestaurantMenuTimestamp(
        @Path("restaurantId") restaurantId: String
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