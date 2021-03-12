package com.lacker.staff.data.storage.user

data class User(
    val id: String,
    val name: String,
    val surname: String,
    val email: String,
    val token: String,
    val fullPhotoUrl: String // TODO add restaurant data!
) {

    companion object {
        private val EMPTY_INSTANCE = User("", "", "", "", "", "")
        fun empty() = EMPTY_INSTANCE
    }

    fun isEmpty(): Boolean = (this == empty())

}
