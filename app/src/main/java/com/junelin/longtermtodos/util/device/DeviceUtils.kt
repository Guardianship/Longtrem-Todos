package com.junelin.longtermtodos.util.device

import android.os.Build

object DeviceUtils {

    private val ONEPLUS_BRANDS = setOf("OnePlus", "ONEPLUS", "oneplus")
    private val OPPO_BRANDS = setOf("OPPO", "oppo", "Oppo")
    private val REALME_BRANDS = setOf("realme", "Realme", "REALME")

    fun isOnePlus(): Boolean {
        return Build.BRAND in ONEPLUS_BRANDS || Build.MANUFACTURER in ONEPLUS_BRANDS
    }

    fun isOppo(): Boolean {
        return Build.BRAND in OPPO_BRANDS || Build.MANUFACTURER in OPPO_BRANDS
    }

    fun isRealme(): Boolean {
        return Build.BRAND in REALME_BRANDS || Build.MANUFACTURER in REALME_BRANDS
    }

    fun isColorOS(): Boolean {
        return getRomVersion().contains("ColorOS", ignoreCase = true) ||
                getRomVersion().contains("OxygenOS", ignoreCase = true) ||
                isOppo() || isOnePlus() || isRealme()
    }

    fun getRomVersion(): String {
        return try {
            Build.DISPLAY ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    fun getDeviceInfo(): String {
        return "${Build.BRAND} ${Build.MODEL} (${Build.DISPLAY})"
    }
}
