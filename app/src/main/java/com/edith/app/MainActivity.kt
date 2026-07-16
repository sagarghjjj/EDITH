package com.edith.app

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView

class MainActivity : AppCompatActivity() {

    private lateinit var permissionManager: PermissionManager
    private lateinit var flashlightController: FlashlightController
    private lateinit var bluetoothController: BluetoothController
    private lateinit var batteryInfoProvider: BatteryInfoProvider
    private lateinit var callController: CallController
    private lateinit var smsController: SmsController
    private lateinit var cameraController: CameraController
    private lateinit var speechOutput: SpeechOutput
    private lateinit var speechInput: SpeechInput
    private lateinit var commandProcessor: CommandProcessor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionManager = PermissionManager(this)
        flashlightController = FlashlightController(this)
        bluetoothController = BluetoothController(this)
        batteryInfoProvider = BatteryInfoProvider(this)
        callController = CallController(this)
        smsController = SmsController()
        cameraController = CameraController(this, this)
        speechOutput = SpeechOutput(this)
        speechInput = SpeechInput(this)
        commandProcessor = CommandProcessor(flashlightController, speechOutput)

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
        val smsNumberInput = findViewById<EditText>(R.id.etSmsNumber)
        val smsMessageInput = findViewById<EditText>(R.id.etSmsMessage)
        val sendSmsButton = findViewById<Button>(R.id.btnSendSms)
        val smsStatus = findViewById<TextView>(R.id.tvSmsStatus)
        val startCameraButton = findViewById<Button>(R.id.btnStartCamera)
        val previewView = findViewById<PreviewView>(R.id.previewView)
        val takePhotoButton = findViewById<Button>(R.id.btnTakePhoto)
        val cameraStatus = findViewById<TextView>(R.id.tvCameraStatus)
        val testVoiceButton = findViewById<Button>(R.id.btnTestVoice)
        val listenButton = findViewById<Button>(R.id.btnListen)
        val heardText = findViewById<TextView>(R.id.tvHeardText)

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

        sendSmsButton.setOnClickListener {
            val number = smsNumberInput.text.toString().trim()
            val message = smsMessageInput.text.toString().trim()
            if (number.isEmpty() || message.isEmpty()) {
                smsStatus.text = "Enter both a number and a message"
                return@setOnClickListener
            }
            permissionManager.requestPermission(Manifest.permission.SEND_SMS) { granted ->
                if (granted) {
                    val success = smsController.sendSms(number, message)
                    smsStatus.text = if (success) {
                        "SMS sent to $number"
                    } else {
                        "Failed to send SMS"
                    }
                } else {
                    smsStatus.text = "SMS permission denied"
                }
            }
        }

        startCameraButton.setOnClickListener {
            permissionManager.requestPermission(Manifest.permission.CAMERA) { granted ->
                if (granted) {
                    cameraController.startCamera(previewView) { error ->
                        cameraStatus.text = error
                    }
                    cameraStatus.text = "Camera started"
                } else {
                    cameraStatus.text = "Camera permission denied"
                }
            }
        }

        takePhotoButton.setOnClickListener {
            cameraController.takePhoto { success, message ->
                cameraStatus.text = message
            }
        }

        testVoiceButton.setOnClickListener {
            speechOutput.speak("Hello. EDITH voice output is working.")
        }

        listenButton.setOnClickListener {
            permissionManager.requestPermission(Manifest.permission.RECORD_AUDIO) { granted ->
                if (granted) {
                    heardText.text = "Listening..."
                    speechInput.startListening(
                        onResult = { text ->
                            heardText.text = "Heard: \"$text\""
                            commandProcessor.process(text)
                        },
                        onError = { error ->
                            heardText.text = error
                        }
                    )
                } else {
                    heardText.text = "Microphone permission denied"
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        flashlightController.turnOff()
    }

    override fun onDestroy() {
        super.onDestroy()
        speechOutput.shutdown()
        speechInput.destroy()
    }
}
