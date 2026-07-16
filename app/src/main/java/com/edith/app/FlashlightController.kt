package com.edith.app

import android.content.Context
import android.hardware.camera2.CameraManager
import android.util.Log

/**
 * Controls the device's rear flashlight (torch mode) via Camera2 API.
 * No runtime permission needed for torch mode on API 23+.
 */
class FlashlightController(context: Context) {

    private val cameraManager =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private val cameraId: String? by lazy {
        try {
            cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val hasFlash = characteristics.get(
                    android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE
                )
                hasFlash == true
            }
        } catch (e: Exception) {
            Log.e("FlashlightController", "Error finding camera with flash", e)
            null
        }
    }

    var isOn: Boolean = false
        private set

    fun toggle(): Boolean {
        val id = cameraId ?: return false
        return try {
            isOn = !isOn
            cameraManager.setTorchMode(id, isOn)
            isOn
        } catch (e: Exception) {
            Log.e("FlashlightController", "Error toggling flashlight", e)
            isOn = false
            false
        }
    }

    fun turnOff() {
        val id = cameraId ?: return
        try {
            cameraManager.setTorchMode(id, false)
            isOn = false
        } catch (e: Exception) {
            Log.e("FlashlightController", "Error turning off flashlight", e)
        }
    }
}
