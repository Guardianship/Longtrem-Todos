package com.junelin.longtermtodos.util.device

import android.content.ComponentName
import android.content.Context
import android.content.Intent

object AutoStartUtils {

    fun openAutoStartSettings(context: Context) {
        val intents = mutableListOf<Intent>()

        // ColorOS / OxygenOS (OnePlus/OPPO/realme)
        intents.add(Intent().apply {
            component = ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.permission.startup.StartupAppListActivity"
            )
        })
        intents.add(Intent().apply {
            component = ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.startupapp.StartupAppListActivity"
            )
        })
        intents.add(Intent().apply {
            component = ComponentName(
                "com.oppo.safe",
                "com.oppo.safe.permission.startup.StartupAppListActivity"
            )
        })
        intents.add(Intent().apply {
            component = ComponentName(
                "com.oneplus.security",
                "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
            )
        })

        // MIUI
        intents.add(Intent().apply {
            component = ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
        })

        // Samsung
        intents.add(Intent().apply {
            component = ComponentName(
                "com.samsung.android.lool",
                "com.samsung.android.sm.battery.ui.BatteryActivity"
            )
        })

        // Huawei
        intents.add(Intent().apply {
            component = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.optimize.process.ProtectActivity"
            )
        })

        // Vivo
        intents.add(Intent().apply {
            component = ComponentName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
            )
        })

        for (intent in intents) {
            try {
                context.startActivity(intent)
                return
            } catch (_: Exception) {
                // Try next
            }
        }
    }
}
