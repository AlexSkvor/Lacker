package com.lacker.utils.extensions

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment

fun Fragment.vibrate(length: Long = 100L) {
    activity?.vibrate(length)
}

fun Activity.vibrate(length: Long = 100L) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        vibrator.vibrate(VibrationEffect.createOneShot(length, VibrationEffect.DEFAULT_AMPLITUDE))
    else vibrator.vibrate(length)
}