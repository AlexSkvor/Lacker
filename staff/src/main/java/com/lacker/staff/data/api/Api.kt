package com.lacker.staff.data.api

import com.lacker.staff.data.dto.auth.AuthRequest
import com.lacker.staff.data.dto.auth.UserDto
import com.lacker.staff.data.dto.orders.NewOrdersPageResponse
import com.lacker.staff.data.dto.restaurant.RestaurantDto
import com.lacker.staff.data.dto.restaurant.RestaurantInfoResponse
import retrofit2.http.*

interface Api {

    @POST("staff/account/sign-in/")
    suspend fun signIn(
        @Body request: AuthRequest
    ): UserDto

    @GET("restaurants/{restaurantId}/info/")
    suspend fun getRestaurantInfo(
        @Path("restaurantId") restaurantId: String
    ): RestaurantInfoResponse

    @GET("restaurants/list")
    suspend fun getRestaurants(): List<RestaurantDto>

    @GET("restaurants/{restaurantId}/newOrders")
    suspend fun getNewOrders(
        @Path("restaurantId") restaurantId: String,
        @Query("lastReceivedSuborderId") lastReceivedSuborderId: String?,
    ): NewOrdersPageResponse
}