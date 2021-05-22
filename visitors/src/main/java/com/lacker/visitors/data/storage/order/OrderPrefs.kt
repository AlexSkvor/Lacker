package com.lacker.visitors.data.storage.order

import android.content.Context
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import androidx.core.content.edit
import javax.inject.Inject

class OrderPrefs @Inject constructor(
    private val context: Context,
) : OrderStorage {

    private companion object {
        const val ORDER_ID_KEY = "ORDER_ID_KEY"
    }

    private val prefs: Preferences by lazy {
        BinaryPreferencesBuilder(context)
            .name("Läcker_visitors_prefs_order")
            .allowBuildOnBackgroundThread()
            .supportInterProcess(true)
            .build()
    }

    override var orderId: String?
        get() = prefs.getString(ORDER_ID_KEY, null)
        set(value) = prefs.edit { putString(ORDER_ID_KEY, value) }

}