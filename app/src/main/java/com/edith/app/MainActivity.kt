package com.edith.app

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var permissionManager: PermissionManager
    private lateinit var flashlightController: FlashlightController
    private lateinit var bluetoothController: BluetoothController
    private lateinit var batteryInfoProvider: BatteryInfoProvider
    private lateinit var callController: CallController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionManager = PermissionManager(this)
        flashlightController = FlashlightController(this)
        bluetoothController = BluetoothController(this)
        batteryInfoProvider = BatteryInfoProvider(this)
        callController = CallController(this)

        val statusText = findViewById<TextView>(R.id.tvPermissionStatus)
        val testButton = findViewById<Button>(R.id.btnTestPermission)
        val flashlightButton = findViewById<Button>(R.id.btnFlashlight)
        val bluetoothEnableButton = findViewById<Button>(R.id.btnBluetoothEnable)
        val bluetoothSettingsButton = findViewById<Button>(R.id.btnBluetoothSettings)
        val bluetoothStatus = findViewById<TextView>(R.id.tvBluetoothStatus)
        val batteryButton = findViewById<Button>(R.id.btnBatteryInfo)
        val batteryStatus = findViewById<TextView>(R.id.tvBatteryStatus)
        val phoneNumberInput = findViewById<EditText>(R.id.etPhoneNumber)
        val callButton = findViewById<Button>(R.id.btnCall)
        val callStatus = findViewById<TextView>(R.id.tvCallStatus)

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

        batteryButton.setOnClickListener {
            val info = batteryInfoProvider.getBatteryInfo()
            batteryStatus.text = "Battery: ${info.percentage}% | " +
                "Charging: ${if (info.isCharging) "Yes" else "No"} | " +
                "Temp: ${info.temperatureCelsius}°C"
        }

        callButton.setOnClickListener {
            val number = phoneNumberInput.text.toString().trim()
            if (number.isEmpty()) {
                callStatus.text = "Enter a phone number first"
                return@setOnClickListener
            }
            permissionManager.requestPermission(Manifest.permission.CALL_PHONE) { granted ->
                if (granted) {
                    val success = callController.placeCall(number)
                    callStatus.text = if (success) {
                        "Calling $number..."
                    } else {
                        "Failed to place call"
                    }
                } else {
                    callStatus.text = "Call permission denied"
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        flashlightController.turnOff()
    }
}
