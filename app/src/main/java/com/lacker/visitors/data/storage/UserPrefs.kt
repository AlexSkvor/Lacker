package com.lacker.visitors.data.storage

import android.content.Context
import androidx.core.content.edit
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import com.squareup.moshi.Moshi
import com.lacker.utils.extensions.onNull
import javax.inject.Inject

class UserPrefs @Inject constructor(
    private val context: Context,
    private val json: Moshi
) : UserStorage {

    private companion object {
        const val TOKEN_KEY = "TOKEN_KEY"
        const val ROLE_KEY = "ROLE_KEY"
        const val ID_KEY = "ID_KEY"
        const val EMAIL_KEY = "EMAIL_KEY"
        const val FULL_NAME_KEY = "FULL_NAME_KEY"
        const val CITY_KEY = "CITY_KEY"
    }

    private val empty by lazy { User.empty() }

    private val prefs: Preferences by lazy {
        BinaryPreferencesBuilder(context)
            .name("Läcker_visitors_prefs_user")
            .allowBuildOnBackgroundThread()
            .supportInterProcess(true)
            .build()
    }

    override var user: User
        get() = if (token.isEmpty()) empty else User(
            id = id,
            fullName = fullName,
            email = email,
            city = city,
            role = role,
            token = token
        )
        set(value) {
            id = value.id
            token = value.token
            role = value.role
            email = value.email
            fullName = value.fullName
            city = value.city
        }

    private var id: Long
        get() = prefs.getLong(ID_KEY, empty.id)
        set(value) = prefs.edit { putLong(ID_KEY, value) }

    private var token: String
        get() = prefs.getString(TOKEN_KEY, empty.token).orEmpty()
        set(value) = prefs.edit { putString(TOKEN_KEY, value) }

    private var role: User.Role
        get() = json.adapter(User.Role::class.java)
            .fromJson(prefs.getString(ROLE_KEY, "").orEmpty())
            .onNull(User.Role.ManagerUser)
        set(value) = prefs.edit {
            putString(ROLE_KEY, json.adapter(User.Role::class.java).toJson(value))
        }

    private var email: String
        get() = prefs.getString(EMAIL_KEY, empty.email).onNull(empty.email)
        set(value) = prefs.edit { putString(EMAIL_KEY, value) }

    private var fullName: String
        get() = prefs.getString(FULL_NAME_KEY, empty.fullName).onNull(empty.fullName)
        set(value) = prefs.edit { putString(FULL_NAME_KEY, value) }

    private var city: String
        get() = prefs.getString(CITY_KEY, empty.city).onNull(empty.city)
        set(value) = prefs.edit { putString(CITY_KEY, value) }

}