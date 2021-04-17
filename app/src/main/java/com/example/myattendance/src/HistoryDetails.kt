package com.example.myattendance.src

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedFile
import com.example.myattendance.R
import com.example.myattendance.utility.CryptographyManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream


class HistoryDetails: AppCompatActivity() {
    private lateinit var attendDetails: StringBuilder
    private lateinit var encryptedFile: EncryptedFile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_details)
        title = "Attendance Details"

        val selected = intent.getStringExtra("filename")
        val attendance = getDir("History", MODE_PRIVATE)
        val history : Array<File> = attendance.listFiles()
        attendDetails = StringBuilder("")
        for (att in history) {
            if (selected == att.name) {
                attendDetails.append(att.inputStream().readBytes().toString(Charsets.UTF_8))
            }
        }
        findViewById<TextView>(R.id.attendance_details).text = attendDetails
    }
}