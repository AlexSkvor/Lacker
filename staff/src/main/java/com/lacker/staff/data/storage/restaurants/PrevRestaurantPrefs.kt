package com.lacker.staff.data.storage.restaurants

import android.content.Context
import androidx.core.content.edit
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import javax.inject.Inject

class PrevRestaurantPrefs @Inject constructor(
    private val context: Context
) : PrevRestaurantStorage {

    private companion object {
        const val RESTAURANT_CODE_KEY = "RESTAURANT_CODE_KEY"
    }

    private val prefs: Preferences by lazy {
        BinaryPreferencesBuilder(context)
            .name("Läcker_staff_prefs_restaurant_ids")
            .allowBuildOnBackgroundThread()
            .supportInterProcess(true)
            .build()
    }

    override var restaurantCode: String?
        get() = prefs.getString(RESTAURANT_CODE_KEY, null)
        set(value) {
            prefs.edit { putString(RESTAURANT_CODE_KEY, value) }
        }
}