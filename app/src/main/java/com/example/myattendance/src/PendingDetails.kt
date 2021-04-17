package com.example.myattendance.src

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedFile
import com.example.myattendance.R
import com.example.myattendance.utility.CryptographyManager
import java.io.ByteArrayOutputStream
import java.io.File

class PendingDetails: AppCompatActivity() {

    private lateinit var attendDetails: StringBuilder
    private lateinit var encryptedFile: EncryptedFile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_details)
        title = "Attendance Details"

        val selected = intent.getStringExtra("filename")
        val attendance = getDir("Pending", AppCompatActivity.MODE_PRIVATE)
        val pending : Array<File> = attendance.listFiles()
        attendDetails = StringBuilder("")
        for (att in pending) {
            if (selected == att.name) {
                encryptedFile = CryptographyManager().getOrCreateEncryptedFile(
                    applicationContext, attendance, att.name
                )

                attendDetails.append(readAttendance(encryptedFile))
            }
        }
        findViewById<TextView>(R.id.attendance_details).text = attendDetails
    }

    private fun readAttendance(file: EncryptedFile): String{
        val inputStream = file.openFileInput()
        val byteArrayOutputStream = ByteArrayOutputStream()
        var nextByte: Int = inputStream.read()
        while (nextByte != -1) {
            byteArrayOutputStream.write(nextByte)
            nextByte = inputStream.read()
        }
        return byteArrayOutputStream.toByteArray().toString(Charsets.UTF_8)
    }
}