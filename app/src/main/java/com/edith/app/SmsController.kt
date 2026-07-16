package com.edith.app

import android.telephony.SmsManager
import android.util.Log

/**
 * Sends SMS messages directly using SEND_SMS permission.
 * Caller (MainActivity) is responsible for checking/requesting permission first.
 */
class SmsController {

    fun sendSms(phoneNumber: String, message: String): Boolean {
        return try {
            val smsManager = SmsManager.getDefault()
            val parts = smsManager.divideMessage(message)
            if (parts.size == 1) {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            } else {
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
            }
            true
        } catch (e: Exception) {
            Log.e("SmsController", "Error sending SMS", e)
            false
        }
    }
}
