package com.lacker.utils.extensions

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.lacker.utils.R

fun Fragment.hasPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(
    requireContext(),
    permission
) == PackageManager.PERMISSION_GRANTED

fun Fragment.onUserForeverDeniedPermission() {
    AlertDialog.Builder(requireContext())
        .setTitle(R.string.permissionRequired)
        .setMessage(R.string.whyNeedPermission)
        .setCancelable(false)
        .setNegativeButton(R.string.cancelCaps) { _, _ -> }
        .setPositiveButton(R.string.settings) { _, _ -> openAppSettings() }
        .create()
        .show()
}

private fun Fragment.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:${requireActivity().packageName}")
    )
    startActivity(intent)
}