package com.edith.app

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * Central permission handling for EDITH.
 * Every feature that needs a runtime permission (camera, calls, SMS, etc.)
 * should go through this class instead of writing its own request logic.
 */
class PermissionManager(private val activity: ComponentActivity) {

    private var pendingSingleCallback: ((Boolean) -> Unit)? = null
    private var pendingMultipleCallback: ((Map<String, Boolean>) -> Unit)? = null

    private val singlePermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        pendingSingleCallback?.invoke(granted)
        pendingSingleCallback = null
    }

    private val multiplePermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        pendingMultipleCallback?.invoke(results)
        pendingMultipleCallback = null
    }

    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) ==
            PackageManager.PERMISSION_GRANTED
    }

    fun hasPermissions(permissions: List<String>): Boolean {
        return permissions.all { hasPermission(it) }
    }

    fun requestPermission(permission: String, onResult: (Boolean) -> Unit) {
        if (hasPermission(permission)) {
            onResult(true)
            return
        }
        pendingSingleCallback = onResult
        singlePermissionLauncher.launch(permission)
    }

    fun requestPermissions(permissions: List<String>, onResult: (Map<String, Boolean>) -> Unit) {
        val missing = permissions.filterNot { hasPermission(it) }
        if (missing.isEmpty()) {
            onResult(permissions.associateWith { true })
            return
        }
        pendingMultipleCallback = onResult
        multiplePermissionLauncher.launch(missing.toTypedArray())
    }
}
