package com.lacker.staff.data.api

import com.lacker.dto.appeal.AppealDto
import com.lacker.dto.common.CheckRequest
import com.lacker.dto.common.DataDto
import com.lacker.dto.common.DataListDto
import com.lacker.dto.common.DateTimeDto
import com.lacker.dto.menu.Menu
import com.lacker.dto.order.CloseOrderBody
import com.lacker.dto.order.Order
import com.lacker.dto.order.OrderWithoutSuborders
import com.lacker.dto.order.SubOrder
import com.lacker.staff.data.dto.auth.AuthRequest
import com.lacker.staff.data.dto.auth.UserDto
import com.lacker.utils.api.auth.AuthHeaderInterceptor
import retrofit2.http.*

interface Api {

    @POST("public/auth/staff")
    suspend fun signIn(
        @Body request: AuthRequest
    ): DataDto<UserDto>

    @GET("api/{restaurantId}/main_menu")
    suspend fun getRestaurantMenu(
        @Path("restaurantId") restaurantId: String
    ): DataDto<Menu>

    @GET("api/{restaurantId}/main_menu")
    suspend fun getRestaurantMenuTimestamp(
        @Path("restaurantId") restaurantId: String,
        @Query("fields") fields: String = "update_time"
    ): DataDto<DateTimeDto>

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @GET("api/{restaurantId}/unchecked_suborders")
    suspend fun getNewOrders(
        @Path("restaurantId") restaurantId: String,
    ): DataListDto<SubOrder>

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @GET("api/{restaurantId}/checked_suborders")
    suspend fun getOldOrders(
        @Path("restaurantId") restaurantId: String,
    ): DataListDto<SubOrder>

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @POST("api/{suborderId}")
    suspend fun acceptSuborder(
        @Path("suborderId") suborderId: String,
        @Body checkRequest: CheckRequest = CheckRequest(),
    )

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @GET("api/{restaurantId}/unchecked_appeals")
    suspend fun getNewAppeals(
        @Path("restaurantId") restaurantId: String,
    ): DataListDto<AppealDto>

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @GET("api/{restaurantId}/checked_appeals")
    suspend fun getOldAppeals(
        @Path("restaurantId") restaurantId: String,
    ): DataListDto<AppealDto>

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @POST("api/{appealId}")
    suspend fun acceptAppeal(
        @Path("appealId") appealId: String,
        @Body checkRequest: CheckRequest = CheckRequest(),
    )

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @GET("api/{restaurantId}/orders")
    suspend fun getOrders(
        @Path("restaurantId") restaurantId: String,
    ): DataListDto<OrderWithoutSuborders>

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @GET("api/{orderId}")
    suspend fun getOrderById(
        @Path("orderId") orderId: String,
    ): DataDto<Order>

    @Headers(AuthHeaderInterceptor.REQUIRES_AUTH)
    @POST("api/{orderId}")
    suspend fun closeOrder(
        @Path("orderId") orderId: String,
        @Body closeOrderBody: CloseOrderBody = CloseOrderBody(),
    )
}