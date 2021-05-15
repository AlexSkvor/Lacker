package com.lacker.utils.api.auth

interface TokenProvider {

    fun getAuthToken(): String?

}