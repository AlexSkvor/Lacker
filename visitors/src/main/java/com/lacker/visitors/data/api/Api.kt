package com.lacker.visitors.data.api

import com.lacker.dto.common.DataDto
import com.lacker.dto.common.DataListDto
import com.lacker.dto.common.DateTimeDto
import com.lacker.dto.common.IdOwner
import com.lacker.dto.menu.Menu
import com.lacker.dto.order.CloseOrderBody
import com.lacker.dto.order.Order
import com.lacker.utils.api.auth.AuthHeaderInterceptor
import com.lacker.visitors.data.dto.appeal.CreateAppealRequest
import com.lacker.visitors.data.dto.auth.GoogleAuthData
import com.lacker.visitors.data.dto.auth.UserFromServer
import com.lacker.visitors.data.dto.order.AddSuborderRequest
import com.lacker.visitors.data.dto.order.CreateOrderRequest
import retrofit2.http.*

interface Api {

    @POST("public/auth/google")
    suspend fun signInWithGoogle(
        @Body request: GoogleAuthData
    ): DataDto<UserFromServer>

    @GET("api/{restaurantId}/main_menu")
    suspend fun getRestaurantMenu(
        @Path("restaurantId") restaurantId: String
    ): DataDto<Menu>

    @GET("api/{restaurantId}/main_menu")
    suspend fun getRestaurantMenuTimestamp(
        @Path("restaurantId") restaurantId: String,
        @Query("fields") fields: String = "update_time"
    ): DataDto<DateTimeDto>

    @GET("api/{restaurantId}/tables")
    suspend fun getTablesOfRestaurant(
        @Path("restaurantId") restaurantId: String,
    ): DataListDto<IdOwner>

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @POST("api/me/make_appeal")
    suspend fun callStaff(
        @Body request: CreateAppealRequest,
    )

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @GET("api/{orderId}")
    suspend fun getOrderById(
        @Path("orderId") orderId: String,
    ): DataDto<Order>

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @POST("api/me/make_order")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): DataDto<Order>

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @POST("api/{orderId}")
    suspend fun closeOrder(
        @Path("orderId") orderId: String,
        @Body closeOrderBody: CloseOrderBody = CloseOrderBody(),
    )

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @POST("api/{orderId}/add_suborder")
    suspend fun addToCurrentOrder(
        @Path("orderId") orderId: String,
        @Body request: AddSuborderRequest,
    ): DataDto<Order>
}