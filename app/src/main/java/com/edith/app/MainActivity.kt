package com.edith.app

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var permissionManager: PermissionManager
    private lateinit var flashlightController: FlashlightController
    private lateinit var bluetoothController: BluetoothController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionManager = PermissionManager(this)
        flashlightController = FlashlightController(this)
        bluetoothController = BluetoothController(this)

        val statusText = findViewById<TextView>(R.id.tvPermissionStatus)
        val testButton = findViewById<Button>(R.id.btnTestPermission)
        val flashlightButton = findViewById<Button>(R.id.btnFlashlight)
        val bluetoothEnableButton = findViewById<Button>(R.id.btnBluetoothEnable)
        val bluetoothSettingsButton = findViewById<Button>(R.id.btnBluetoothSettings)
        val bluetoothStatus = findViewById<TextView>(R.id.tvBluetoothStatus)

        testButton.setOnClickListener {
            permissionManager.requestPermission(Manifest.permission.CAMERA) { granted ->
                statusText.text = if (granted) {
                    "Camera permission: GRANTED"
                } else {
                    "Camera permission: DENIED"
                }
            }
        }

        flashlightButton.setOnClickListener {
            val isNowOn = flashlightController.toggle()
            flashlightButton.text = if (isNowOn) {
                "Turn Flashlight Off"
            } else {
                "Toggle Flashlight"
            }
        }

        bluetoothEnableButton.setOnClickListener {
            permissionManager.requestPermission(Manifest.permission.BLUETOOTH_CONNECT) { granted ->
                if (granted) {
                    bluetoothController.requestEnable { success ->
                        bluetoothStatus.text = if (success) {
                            "Bluetooth: ENABLED"
                        } else {
                            "Bluetooth: user declined or already on"
                        }
                    }
                } else {
                    bluetoothStatus.text = "Bluetooth permission denied"
                }
            }
        }

        bluetoothSettingsButton.setOnClickListener {
            bluetoothController.openBluetoothSettings()
        }
    }

    override fun onStop() {
        super.onStop()
        flashlightController.turnOff()
    }
}
