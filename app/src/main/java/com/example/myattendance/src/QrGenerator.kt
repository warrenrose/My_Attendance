package com.example.myattendance.src

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedFile
import com.example.myattendance.R
import com.example.myattendance.utility.CryptographyManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.zxing.WriterException
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class QrGenerator : AppCompatActivity() {

    private lateinit var attendName: String
    private lateinit var attendanceMessage: String
    private val validationCode: String = "UTAR - Universiti Tunku Abdul Rahman"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_code_generate)
        title = "Attendance QR Code"

        var qrImage = findViewById<ImageView>(R.id.QrHolder)
        val classSel = intent.getStringExtra("class")
        findViewById<TextView>(R.id.class_title).text = classSel

        val acct = GoogleSignIn.getLastSignedInAccount(baseContext)
        acct?.let {
            attendName = it.displayName.toString()
        }

        var dateTimeNow: String = if (android.os.Build.VERSION.SDK_INT >= 26) {
            val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mma")
            val myZone: ZoneId = ZoneId.of("Asia/Singapore")
            val now: ZonedDateTime = ZonedDateTime.now(myZone)
            //val now = LocalDateTime.now()
            dtf.format(now)
        }
        else {
            val formatter = SimpleDateFormat("dd-MM-yyyy hh:mma")
            val date = Date()
            formatter.format(date)
        }

        //QR generated time
        findViewById<TextView>(R.id.class_time).text = dateTimeNow

        val getRegFile: EncryptedFile = CryptographyManager().getOrCreateEncryptedFile(
            applicationContext, applicationContext.filesDir, "registration_info")

        val inputStream = getRegFile.openFileInput()
        val byteArrayOutputStream = ByteArrayOutputStream()
        var nextByte: Int = inputStream.read()
        while (nextByte != -1) {
            byteArrayOutputStream.write(nextByte)
            nextByte = inputStream.read()
        }

        val userRegInfo: String = byteArrayOutputStream.toByteArray().toString(Charsets.UTF_8)

        /*val userRegInfo = this.openFileInput("reg_info").bufferedReader().useLines { lines ->
            lines.fold("") { some, text ->
                "$some\n$text"
            }
        }*/

        /*  If student, qr saves name
            If lecturer, qr doesn't save name  */
        attendanceMessage = if (userRegInfo.contains("student"))
            "$attendName/$classSel/$dateTimeNow/$validationCode"
        else
            "$classSel/$dateTimeNow/$validationCode"

        val qrgEncoder = QRGEncoder(attendanceMessage, null, QRGContents.Type.TEXT, 150)
        try {
            qrImage.setImageBitmap(qrgEncoder.bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }
}
