package com.lacker.staff.data.storage.restaurants

import android.content.Context
import androidx.core.content.edit
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import javax.inject.Inject

class RestaurantPrefs @Inject constructor(
    val context: Context,
) : RestaurantStorage {

    private companion object {
        const val RESTAURANT_ID_KEY = "RESTAURANT_ID_KEY"
    }

    private val prefs: Preferences by lazy {
        BinaryPreferencesBuilder(context)
            .name("Läcker_staff_prefs_restaurant")
            .allowBuildOnBackgroundThread()
            .supportInterProcess(true)
            .build()
    }

    override var restaurant: DomainRestaurant?
        get() {
            return DomainRestaurant(
                id = restaurantId ?: return null
            )
        }
        set(value) {
            restaurantId = value?.id
        }

    private var restaurantId: String?
        get() = prefs.getString(RESTAURANT_ID_KEY, null)
        set(value) = prefs.edit { putString(RESTAURANT_ID_KEY, value) }
}