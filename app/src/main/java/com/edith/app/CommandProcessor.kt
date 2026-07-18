package com.edith.app

/**
 * Matches recognized speech text to EDITH actions.
 * Add new command patterns here as more features are voice-enabled.
 */
class CommandProcessor(
    private val flashlightController: FlashlightController,
    private val speechOutput: SpeechOutput,
    private val appLauncher: AppLauncher
) {

    fun process(text: String) {
        val lower = text.lowercase().trim()

        when {
            lower.startsWith("open ") -> {
                val appName = lower.removePrefix("open ").trim()
                if (appName.isEmpty()) {
                    speechOutput.speak("Which app should I open?")
                    return
                }
                val success = appLauncher.launchApp(appName)
                if (success) {
                    speechOutput.speak("Opening $appName")
                } else {
                    speechOutput.speak("I couldn't find an app called $appName")
                }
            }
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
