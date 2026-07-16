package com.edith.app

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var permissionManager: PermissionManager
    private lateinit var flashlightController: FlashlightController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionManager = PermissionManager(this)
        flashlightController = FlashlightController(this)

        val statusText = findViewById<TextView>(R.id.tvPermissionStatus)
        val testButton = findViewById<Button>(R.id.btnTestPermission)
        val flashlightButton = findViewById<Button>(R.id.btnFlashlight)

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
    }

    override fun onStop() {
        super.onStop()
        flashlightController.turnOff()
    }
}
