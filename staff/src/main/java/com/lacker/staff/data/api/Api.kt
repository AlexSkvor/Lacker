package com.lacker.staff.data.api

import com.lacker.staff.data.dto.auth.AuthRequest
import com.lacker.staff.data.dto.auth.UserDto
import com.lacker.staff.data.dto.restaurant.RestaurantDto
import com.lacker.staff.data.dto.restaurant.RestaurantsInfoRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Api {

    @POST("staff/account/sign-in/")
    suspend fun signIn(
        @Body request: AuthRequest
    ): UserDto

    @GET("restaurants/info/")
    suspend fun getRestaurantsInfo(
        @Body request: RestaurantsInfoRequest
    ): List<RestaurantDto>

}