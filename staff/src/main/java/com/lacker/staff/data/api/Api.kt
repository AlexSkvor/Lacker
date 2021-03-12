package com.lacker.staff.data.api

import com.lacker.staff.data.dto.auth.AuthRequest
import com.lacker.staff.data.dto.auth.UserDto
import com.lacker.staff.data.dto.restaurant.RestaurantDto
import com.lacker.staff.data.dto.restaurant.RestaurantInfoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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

}