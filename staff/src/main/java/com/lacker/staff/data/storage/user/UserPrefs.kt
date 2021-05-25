package com.lacker.staff.data.storage.user

import android.content.Context
import androidx.core.content.edit
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import com.lacker.utils.extensions.onNull
import javax.inject.Inject

class UserPrefs @Inject constructor(
    private val context: Context
) : UserStorage {

    private companion object {
        const val TOKEN_KEY = "TOKEN_KEY"
        const val ID_KEY = "ID_KEY"
        const val EMAIL_KEY = "EMAIL_KEY"
        const val NAME_KEY = "NAME_KEY"
        const val SURNAME_KEY = "SURNAME_KEY"
        const val RESTAURANT_ID_KEY = "RESTAURANT_ID_KEY"
    }

    private val empty by lazy { User.empty() }

    private val prefs: Preferences by lazy {
        BinaryPreferencesBuilder(context)
            .name("Läcker_staff_prefs_user")
            .allowBuildOnBackgroundThread()
            .supportInterProcess(true)
            .build()
    }

    override var user: User
        get() = if (token.isEmpty()) empty else User(
            id = id,
            name = name,
            surname = surname,
            email = email,
            token = token,
            restaurantId = restaurantId,
        )
        set(value) {
            id = value.id
            name = value.name
            surname = value.surname
            email = value.email
            token = value.token
            restaurantId = value.restaurantId
        }


    private var token: String
        get() = prefs.getString(TOKEN_KEY, empty.token).onNull(empty.token)
        set(value) = prefs.edit { putString(TOKEN_KEY, value) }

    private var id: String
        get() = prefs.getString(ID_KEY, empty.id).onNull(empty.id)
        set(value) = prefs.edit { putString(ID_KEY, value) }

    private var email: String
        get() = prefs.getString(EMAIL_KEY, empty.email).onNull(empty.email)
        set(value) = prefs.edit { putString(EMAIL_KEY, value) }

    private var name: String
        get() = prefs.getString(NAME_KEY, empty.name).onNull(empty.name)
        set(value) = prefs.edit { putString(NAME_KEY, value) }

    private var surname: String
        get() = prefs.getString(SURNAME_KEY, empty.surname).onNull(empty.surname)
        set(value) = prefs.edit { putString(SURNAME_KEY, value) }

    private var restaurantId: String
        get() = prefs.getString(RESTAURANT_ID_KEY, empty.restaurantId).onNull(empty.restaurantId)
        set(value) = prefs.edit { putString(RESTAURANT_ID_KEY, value) }

}