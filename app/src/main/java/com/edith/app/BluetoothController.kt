package com.edith.app

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

/**
 * Controls Bluetooth state.
 * Enabling uses the system "Allow EDITH to turn on Bluetooth?" dialog (still permitted).
 * Disabling programmatically is blocked since Android 13, so we open Settings instead.
 */
class BluetoothController(private val activity: ComponentActivity) {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        manager?.adapter
    }

    private var pendingEnableCallback: ((Boolean) -> Unit)? = null

    private val enableBluetoothLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val success = result.resultCode == android.app.Activity.RESULT_OK
        pendingEnableCallback?.invoke(success)
        pendingEnableCallback = null
    }

    fun isEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun requestEnable(onResult: (Boolean) -> Unit) {
        if (isEnabled()) {
            onResult(true)
            return
        }
        pendingEnableCallback = onResult
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableBluetoothLauncher.launch(enableIntent)
    }

    fun openBluetoothSettings() {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        activity.startActivity(intent)
    }
}
