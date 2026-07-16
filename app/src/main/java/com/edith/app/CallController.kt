package com.edith.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telecom.TelecomManager
import android.util.Log

/**
 * Places phone calls directly using CALL_PHONE permission.
 * Caller (MainActivity) is responsible for checking/requesting permission first.
 */
class CallController(private val context: Context) {

    fun placeCall(phoneNumber: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e("CallController", "Error placing call", e)
            false
        }
    }
}
