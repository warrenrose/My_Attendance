package com.example.myattendance.utility

import android.content.Context
import android.widget.Toast
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.google.android.gms.auth.api.signin.GoogleSignIn
import java.io.File
import java.nio.charset.StandardCharsets


class Registration {
    private lateinit var userIdentity: String

    fun hasRegistered(context: Context): Boolean {
        val file = File(context.filesDir, "registration_info")
        if (file.exists()) {
            return true
        }
        return false
    }

    fun register (context: Context) {

        val acct = GoogleSignIn.getLastSignedInAccount(context)
        if (acct != null) {
            acct.email?.let {
                userIdentity = if (acct.email!!.contains("edu.my")) {
                    "lecturer"
                } else
                    "student"
            }

            val regContents =
                acct.displayName + System.getProperty("line.separator") + userIdentity

            // Create a file with this name, or replace an entire existing file
            // that has the same name. Note that you cannot append to an existing file,
            // and the file name cannot contain path separators.
            val fileToWrite = "registration_info"
            val encryptedFile = CryptographyManager().getOrCreateEncryptedFile(context, context.filesDir, fileToWrite)

            val fileContent = regContents
                .toByteArray(StandardCharsets.UTF_8)
            encryptedFile.openFileOutput().apply {
                write(fileContent)
                flush()
                close()
            }

            /*val filename = "reg_info"
            var userIdentity = ""
            val acct = GoogleSignIn.getLastSignedInAccount(context)
            if (acct != null) {
                acct.email?.let {
                    userIdentity = if (acct.email!!.contains("edu.my")) {
                        "lecturer"
                    } else
                        "student"
                }
                val regContents =
                    acct.displayName + System.getProperty("line.separator") + userIdentity
                context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                    it.write(regContents.toByteArray())
                }
            }*/
        }
        Toast.makeText(context, "User Registration Successful", Toast.LENGTH_LONG).show()
    }
}