package com.alexskvor.appblank.data.storage

data class User(
    val id: Long,
    val fullName: String,
    val email: String,
    val city: String,
    val role: Role,
    val token: String
) {

    enum class Role {
        ManagerUser, Admin
    }

    companion object {
        private val EMPTY_INSTANCE = User(-1, "", "", "", Role.ManagerUser, "")
        fun empty() = EMPTY_INSTANCE
    }

    fun isEmpty(): Boolean = (this == empty())

}