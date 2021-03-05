package com.lacker.staff.data.api

import com.lacker.staff.data.dto.auth.AuthRequest
import com.lacker.staff.data.dto.auth.UserDto
import com.lacker.staff.data.dto.restaurant.RestaurantDto
import com.lacker.staff.data.dto.restaurant.RestaurantsInfoRequest
import kotlinx.coroutines.delay
import java.util.*
import kotlin.random.Random

class FakeApi : Api {

    private val restaurantCodes = listOf(
        "rest-pizza",
        "cafe-tessa",
        "shaurma",
        "test"
    )

    private val photos = listOf(
        "https://avatars.mds.yandex.net/get-altay/4719974/2a0000017758d235c699eab1a0d4c31cf9af/L",
        "https://avatars.mds.yandex.net/get-altay/979642/2a00000160e88087694502bf3d8f8d62986c/L",
        "https://avatars.mds.yandex.net/get-altay/902827/2a0000016287ba5eb104cf6c0018333fcca3/L",
        "https://avatars.mds.yandex.net/get-altay/4374841/2a000001775a424463203451600901de654d/L",
        "https://avatars.mds.yandex.net/get-altay/2714499/2a00000173b9f2415014609f311fd1fc9ad4/L"
    )

    private val ids = listOf(
        "2a40922e-4a7d-4d1a-9d3d-9d1c388488b2",
        "2c07ee4f-ac6c-4598-a50b-432ac4bd56c6",
        "5dfe5b86-2e1c-4428-9c24-d2f5d292d430",
        "0f5f253d-2328-4380-a30f-c11786623810"
    )

    private data class BackendUser(
        val id: String,
        val name: String,
        val surname: String,
        val email: String,
        val token: String,
        val fullPhotoUrl: String,
        val password: String
    ) {
        fun toDto() = UserDto(
            id = id,
            name = name,
            surname = surname,
            email = email,
            token = token,
            fullPhotoUrl = fullPhotoUrl
        )
    }

    private val restaurants: List<RestaurantDto> by lazy {
        require(photos.size >= restaurantCodes.size)
        require(ids.size >= restaurantCodes.size)
        List(restaurantCodes.size) { i ->
            RestaurantDto(
                id = ids[i],
                code = restaurantCodes[i],
                name = restaurantCodes[i].toUpperCase(Locale.getDefault()),
                fullPhotoUrl = photos[i],
                addressString = "Code name ${restaurantCodes[i]}; Поселок Подковерные войны, переулок Заулочкина дом 33 корпус А подъезд 13"
            )
        }
    }

    private val users: Map<String, List<BackendUser>> by lazy {
        val map = mutableMapOf<String, List<BackendUser>>()

        restaurants.forEach {
            val usersNumber = Random.nextInt(3, 10)
            val usersList = List(usersNumber) { i ->
                BackendUser(
                    id = UUID.randomUUID().toString(),
                    name = "Alex $i",
                    surname = "Skvortsov $i",
                    email = it.code + i + "@" + it.code.substringBefore(' ') + ".ru",
                    token = UUID.randomUUID().toString(),
                    fullPhotoUrl = "https://i.ytimg.com/vi/Yh5whB-37HY/hqdefault_live.jpg",
                    password = it.code + i
                )
            }
            map[it.id] = usersList
        }

        map
    }

    override suspend fun signIn(request: AuthRequest): UserDto {
        delay(Random.nextLong(300, 2000))
        possiblyThrow()
        val user = users[request.restaurantId]?.firstOrNull {
            it.email == request.email && it.password == request.password
        }
        return user?.toDto() ?: throw Exception("Wrong email or restaurantId or password")
    }

    override suspend fun getRestaurantsInfo(request: RestaurantsInfoRequest): List<RestaurantDto> {
        delay(Random.nextLong(300, 2000))
        possiblyThrow()

        return restaurants.filter { it.code in request.codes }
    }

    private fun possiblyThrow(always: Boolean = false) {
        if (Random.nextInt(0, 100) > 90 || always) throw Exception()
    }
}