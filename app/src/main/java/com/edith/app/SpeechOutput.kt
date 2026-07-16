package com.edith.app

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Handles EDITH speaking responses aloud using Android's built-in TTS engine.
 */
class SpeechOutput(context: Context) {

    private var isReady = false
    private var pendingText: String? = null

    private val tts: TextToSpeech = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            isReady = true
            pendingText?.let { speak(it) }
            pendingText = null
        } else {
            Log.e("SpeechOutput", "TTS initialization failed")
        }
    }

    init {
        tts.language = Locale.US
    }

    fun speak(text: String) {
        if (!isReady) {
            pendingText = text
            return
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "edith_utterance")
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
