package com.edith.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

/**
 * Converts spoken audio into text using Android's built-in SpeechRecognizer.
 * Requires internet connection and RECORD_AUDIO permission (checked by caller).
 */
class SpeechInput(private val context: Context) {

    private var recognizer: SpeechRecognizer? = null

    fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition not available on this device")
            return
        }

        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        }

        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull().orEmpty()
                onResult(text)
            }

            override fun onError(error: Int) {
                Log.e("SpeechInput", "Recognition error code: $error")
                onError("Recognition error (code $error)")
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer?.startListening(intent)
    }

    fun destroy() {
        recognizer?.destroy()
cat > app/src/main/java/com/edith/app/CommandProcessor.kt << 'EOF'
package com.edith.app

/**
 * Matches recognized speech text to EDITH actions.
 * Add new command patterns here as more features are voice-enabled.
 */
class CommandProcessor(
    private val flashlightController: FlashlightController,
    private val speechOutput: SpeechOutput
) {

    fun process(text: String) {
        val lower = text.lowercase().trim()

        when {
            lower.contains("turn on") && lower.contains("flashlight") -> {
                if (!flashlightController.isOn) {
                    flashlightController.toggle()
                }
                speechOutput.speak("Turning on the flashlight")
            }
            lower.contains("turn off") && lower.contains("flashlight") -> {
                if (flashlightController.isOn) {
                    flashlightController.toggle()
                }
                speechOutput.speak("Turning off the flashlight")
            }
            else -> {
                speechOutput.speak("Sorry, I didn't understand that command")
            }
        }
    }
}
