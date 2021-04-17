package com.example.myattendance.utility

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.nio.charset.Charset

class Biometric {

    private lateinit var dataToProcess: String
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var readyToEncrypt: Boolean = false
    private lateinit var cryptographyManager: CryptographyManager
    private lateinit var secretKeyName: String
    private lateinit var ciphertext:ByteArray
    private lateinit var initializationVector: ByteArray

    fun createBiometricPrompt(activity: AppCompatActivity, context: Context, action: String): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d(TAG, "$errorCode :: $errString")

            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d(TAG, "Authentication failed. Please try to scan again.")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d(TAG, "Authentication was successful")
                if(action == "Encrypt") {
                    processData(dataToProcess, result.cryptoObject)
                }
                else if(action == "register") {
                    Registration().register(context)
                }
            }
        }

        //The API requires the client/Activity context for displaying the prompt
        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        return biometricPrompt
    }

    fun createPromptInfo(title : String, subtitle: String,
                                 description: String, prompt_use_app_pass: String): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
                // e.g. "Sign In"
                .setTitle(title)
                // e.g. "Biometric for My App"
                .setSubtitle(subtitle)
                // e.g. "Confirm biometric to continue"
                .setDescription(description)
                .setConfirmationRequired(false)
                .setNegativeButtonText(prompt_use_app_pass)
                // .setDeviceCredentialAllowed(true) // Allow PIN/pattern/password authentication.
                // Also note that setDeviceCredentialAllowed and setNegativeButtonText are
                // incompatible so that if you uncomment one you must comment out the other
                .build()
    }

    fun authenticateToEncrypt(appContext: Context, data: String) {
        readyToEncrypt = true
        if (BiometricManager.from(appContext).canAuthenticate() == BiometricManager
                .BIOMETRIC_SUCCESS) {
            dataToProcess = data
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    fun authenticateToDecrypt(appContext: Context, data: String) {
        readyToEncrypt = false
        if (BiometricManager.from(appContext).canAuthenticate() == BiometricManager
                .BIOMETRIC_SUCCESS) {
            dataToProcess = data
            val cipher = cryptographyManager.getInitializedCipherForDecryption(secretKeyName,initializationVector)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun processData(attendanceData: String, cryptoObject: BiometricPrompt.CryptoObject?): String {
        return if (readyToEncrypt) {
            val encryptedData = cryptographyManager.encryptData(attendanceData, cryptoObject?.cipher!!)
            ciphertext = encryptedData.ciphertext
            initializationVector = encryptedData.initializationVector
            String(ciphertext, Charset.forName("UTF-8"))
        } else {
            cryptographyManager.decryptData(ciphertext, cryptoObject?.cipher!!)
        }
    }

    companion object {
        private const val TAG = "My Attendance"
    }

}