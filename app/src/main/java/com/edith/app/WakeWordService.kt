package com.edith.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat

class WakeWordService : Service() {

    private lateinit var speechInput: SpeechInput
    private lateinit var speechOutput: SpeechOutput
    private lateinit var commandProcessor: CommandProcessor
    private lateinit var flashlightController: FlashlightController
    private val handler = Handler(Looper.getMainLooper())

    private var isRunning = false
    private var attemptCount = 0

    companion object {
        const val CHANNEL_ID = "edith_wake_word_channel"
        const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        speechInput = SpeechInput(this)
        speechOutput = SpeechOutput(this)
        flashlightController = FlashlightController(this)
        commandProcessor = CommandProcessor(flashlightController, speechOutput)

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("Starting..."))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true
            handler.postDelayed({ listenForWakeWord() }, 800)
        }
        return START_STICKY
    }

    private fun listenForWakeWord() {
        if (!isRunning) return
        attemptCount++
        updateNotification("Listening... (attempt $attemptCount)")

        speechInput.startListening(
            onResult = { text ->
                val lower = text.lowercase()
                updateNotification("Heard: \"$text\"")
                if ((lower.contains("edith") || lower.contains("edit") || lower.contains("eddie") || lower.contains("eaton"))) {
                    speechOutput.speak("Yes?")
                    handler.postDelayed({ listenForCommand() }, 1500)
                } else {
                    handler.postDelayed({ listenForWakeWord() }, 800)
                }
            },
            onError = { error ->
                updateNotification("Error: $error")
                handler.postDelayed({ listenForWakeWord() }, 1500)
            }
        )
    }

    private fun listenForCommand() {
        if (!isRunning) return
        updateNotification("Listening for command...")

        speechInput.startListening(
            onResult = { text ->
                commandProcessor.process(text)
                handler.postDelayed({ listenForWakeWord() }, 1500)
            },
            onError = { error ->
                updateNotification("Command error: $error")
                handler.postDelayed({ listenForWakeWord() }, 1500)
            }
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "EDITH Wake Word Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("EDITH")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(text))
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        speechInput.destroy()
        speechOutput.shutdown()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
