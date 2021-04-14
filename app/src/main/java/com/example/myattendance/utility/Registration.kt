package com.example.myattendance.utility

import android.content.Context
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import java.io.File


class Registration {

    fun hasRegistered(context: Context): Boolean {
        val file = File(context.filesDir, "reg_info")
        if (file.exists()) {
            return true
        }
        return false
    }

    fun register (context: Context) {
            val filename = "reg_info"
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
            }
        Toast.makeText(context, "User Registration Successful", Toast.LENGTH_SHORT).show()
    }
}