package com.edith.app

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionManager = PermissionManager(this)

        val statusText = findViewById<TextView>(R.id.tvPermissionStatus)
        val testButton = findViewById<Button>(R.id.btnTestPermission)

        testButton.setOnClickListener {
            permissionManager.requestPermission(Manifest.permission.CAMERA) { granted ->
                statusText.text = if (granted) {
                    "Camera permission: GRANTED"
                } else {
                    "Camera permission: DENIED"
                }
            }
        }
    }
}
