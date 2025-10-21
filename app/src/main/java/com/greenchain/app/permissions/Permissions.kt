package com.greenchain.app.permissions

import android.Manifest

/**
 * Centralized list of runtime permissions used in GreenChain.
 * Makes it easy to request and check them consistently across the app.
 */
object Permissions {
    const val CAMERA = Manifest.permission.CAMERA
    const val LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    // Android 13 + only
    const val NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"
}
