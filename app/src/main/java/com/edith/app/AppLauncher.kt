package com.edith.app

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log

/**
 * Launches installed apps by matching a spoken app name against
 * the device's installed app list.
 */
class AppLauncher(private val context: Context) {

    fun launchApp(spokenName: String): Boolean {
        val packageManager = context.packageManager
        val installedApps: List<ApplicationInfo> = packageManager.getInstalledApplications(0)

        val target = spokenName.lowercase().trim()

        val match = installedApps.firstOrNull { appInfo ->
            val label = packageManager.getApplicationLabel(appInfo).toString().lowercase()
            label.contains(target) || target.contains(label)
        }

        if (match == null) {
            Log.e("AppLauncher", "No app found matching: $spokenName")
            return false
        }

        val launchIntent = packageManager.getLaunchIntentForPackage(match.packageName)
        return if (launchIntent != null) {
            context.startActivity(launchIntent)
            true
        } else {
            Log.e("AppLauncher", "App found but has no launch intent: ${match.packageName}")
            false
        }
    }
}
