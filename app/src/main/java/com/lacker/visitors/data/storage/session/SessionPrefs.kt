package com.lacker.visitors.data.storage.session

import android.content.Context
import androidx.core.content.edit
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import javax.inject.Inject

class SessionPrefs @Inject constructor(
    private val context: Context
) : SessionStorage {

    override var session: Session?
        get() {
            return Session(
                restaurantId = restaurantId ?: return null,
                tableId = tableId ?: return null,
                startTimeMillis = startSessionTimeInMillis ?: return null
            )
        }
        set(value) {
            restaurantId = value?.restaurantId
            tableId = value?.tableId
            startSessionTimeInMillis = value?.startTimeMillis
        }

    private companion object {
        private const val RESTAURANT_ID_KEY = "RESTAURANT_ID_KEY"
        private const val TABLE_ID_KEY = "TABLE_ID_KEY"
        private const val START_SESSION_KEY = "START_SESSION_KEY"
    }

    private val prefs: Preferences by lazy {
        BinaryPreferencesBuilder(context)
            .name("Läcker_visitors_prefs_session")
            .allowBuildOnBackgroundThread()
            .supportInterProcess(true)
            .build()
    }

    private var restaurantId: String?
        get() = prefs.getString(RESTAURANT_ID_KEY, null)
        set(value) = prefs.edit { putString(RESTAURANT_ID_KEY, value) }

    private var tableId: String?
        get() = prefs.getString(TABLE_ID_KEY, null)
        set(value) = prefs.edit { putString(TABLE_ID_KEY, value) }

    private var startSessionTimeInMillis: Long?
        get() {
            val value = prefs.getLong(START_SESSION_KEY, -1)
            return if (value > 0) value else null
        }
        set(value) = prefs.edit { putLong(START_SESSION_KEY, value ?: -1L) }
}