package com.lacker.staff.data.storage.restaurants

import android.content.Context
import androidx.core.content.edit
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import javax.inject.Inject

class SignedBeforeRestaurantsPrefs @Inject constructor(
    private val context: Context
) : SignedBeforeRestaurantsStorage {

    private companion object {
        const val CODES_SET_KEY = "CODES_SET_KEY"
        const val IDS_TO_EMAIL_MAP_KEY = "IDS_TO_EMAIL_MAP_KEY"

        const val KEY_VALUE_DIVIDER = "|||||"
        const val ELEMENTS_DIVIDER = "$$$$$"
    }

    private val prefs: Preferences by lazy {
        BinaryPreferencesBuilder(context)
            .name("Läcker_staff_prefs_restaurant_ids")
            .allowBuildOnBackgroundThread()
            .supportInterProcess(true)
            .build()
    }

    override var restaurantCodes: Set<String>
        get() = prefs.getStringSet(CODES_SET_KEY, emptySet()).orEmpty()
        set(value) {
            prefs.edit { putStringSet(CODES_SET_KEY, value) }
        }

    override fun addEmail(restaurantId: String, email: String) {
        val tmpMap = restaurantIdToEmailMap.orEmpty().toMutableMap()
        tmpMap[restaurantId] = email
        restaurantIdToEmailMap = tmpMap
    }

    override fun getEmail(restaurantId: String): String? =
        restaurantIdToEmailMap?.get(restaurantId)

    private var restaurantIdToEmailMap: Map<String, String>? = null
        get() = if (field == null) prefs.getString(IDS_TO_EMAIL_MAP_KEY, "").orEmpty()
            .split(ELEMENTS_DIVIDER)
            .filterNot { it.isEmpty() }
            .map { it.substringBefore(KEY_VALUE_DIVIDER) to it.substringAfter(KEY_VALUE_DIVIDER) }
            .toMap()
        else field
        set(value) {
            field = value
            if (value != null) {
                val str = value.entries.toList()
                    .joinToString(ELEMENTS_DIVIDER) { it.key + KEY_VALUE_DIVIDER + it.value }
                prefs.edit { putString(IDS_TO_EMAIL_MAP_KEY, str) }
            }
        }
}