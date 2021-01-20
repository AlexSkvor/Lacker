package com.lacker.visitors.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import com.lacker.visitors.data.dto.auth.LoginResponse
import com.lacker.visitors.data.dto.auth.UserLoginRequest
import com.lacker.visitors.data.dto.common.DateTimeResponse
import com.lacker.visitors.data.dto.menu.Menu
import com.lacker.visitors.features.auth.main.GoogleAuthData
import retrofit2.http.GET
import retrofit2.http.Path
import java.time.LocalDateTime

interface Api {

    @POST("account/sign-in/lacker")
    suspend fun signInWithLackerAccount(
        @Body request: UserLoginRequest
    ): LoginResponse

    @POST("account/sign-in/google")
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

    @GET("restaurants/{restaurantId}/hasTable/{tableId}")
    suspend fun checkRestaurantExistsAndHasTable(
        @Path("restaurantId") restaurantId: String,
        @Path("tableId") tableId: String
    )
}