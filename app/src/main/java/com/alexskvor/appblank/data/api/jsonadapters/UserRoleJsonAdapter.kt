package com.alexskvor.appblank.data.api.jsonadapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.alexskvor.appblank.data.storage.User

class UserRoleJsonAdapter {

    private val map = listOf(
        User.Role.ManagerUser to "user",
        User.Role.Admin to "admin",
    )

    @ToJson
    fun toJson(value: User.Role): String {
        return requireNotNull(map.firstOrNull { it.first == value }?.second) {
            "toJson($value) is not implemented yet!"
        }
    }

    @FromJson
    fun fromJson(value: String): User.Role {
        return requireNotNull(map.firstOrNull { it.second == value }?.first) {
            "fromJson($value) is not implemented yet!"
        }
    }
}