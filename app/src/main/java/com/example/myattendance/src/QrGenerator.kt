package com.example.myattendance.src

import android.os.Bundle
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import com.example.myattendance.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.zxing.WriterException
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class QrGenerator : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_code_generate)

        var qrImage = findViewById<ImageView>(R.id.QrHolder)
        val classSel = intent.getStringExtra("class")
        var attendName = ""

        val acct = GoogleSignIn.getLastSignedInAccount(baseContext)
        acct?.let {
            attendName = it.displayName.toString()
        }

        var dateTimeNow: String = if (android.os.Build.VERSION.SDK_INT >= 26) {
            val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
            val now = LocalDateTime.now()
            dtf.format(now)
        }
        else {
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val date = Date()
            formatter.format(date)
        }

        var attendanceMessage = attendName + File.pathSeparator + classSel + File.pathSeparator + dateTimeNow

        val qrgEncoder = QRGEncoder(attendanceMessage, null, QRGContents.Type.TEXT, 150)
        try {
            qrImage.setImageBitmap(qrgEncoder.bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }
}
