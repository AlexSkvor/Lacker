package com.lacker.staff.data.storage.user

import com.lacker.utils.api.auth.TokenProvider

interface UserStorage : TokenProvider {
    var user: User

    override fun getAuthToken(): String? = if (user.isEmpty()) null else user.token
}