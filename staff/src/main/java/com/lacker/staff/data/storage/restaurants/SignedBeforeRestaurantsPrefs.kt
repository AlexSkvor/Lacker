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
        const val IDS_SET_KEY = "IDS_SET_KEY"
    }

    private val prefs: Preferences by lazy {
        BinaryPreferencesBuilder(context)
            .name("Läcker_staff_prefs_restaurant_ids")
            .allowBuildOnBackgroundThread()
            .supportInterProcess(true)
            .build()
    }

    override var restaurantIds: Set<String>
        get() = prefs.getStringSet(IDS_SET_KEY, emptySet()).orEmpty()
        set(value) {
            prefs.edit { putStringSet(IDS_SET_KEY, value) }
        }
}