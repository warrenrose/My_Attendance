package com.example.myattendance

import android.os.Bundle
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.WriterException


class QrGenerator : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_code_generate)

        val qrImage = findViewById<ImageView>(R.id.QrHolder)
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        val qrgEncoder = QRGEncoder(message, null, QRGContents.Type.TEXT, 150)
        try {
            qrImage.setImageBitmap(qrgEncoder.bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }
}
