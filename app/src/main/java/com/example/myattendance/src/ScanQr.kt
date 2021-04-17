package com.example.myattendance.src

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.security.crypto.EncryptedFile
import com.budiyev.android.codescanner.*
import com.example.myattendance.R
import com.example.myattendance.utility.Connection
import com.example.myattendance.utility.CryptographyManager
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


private const val CAMERA_REQUEST_CODE = 101

class ScanQr : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private lateinit var attendanceInfo: String
    private lateinit var encryptedFile: EncryptedFile
    private lateinit var directory: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_scanner)
        title = "Attendance QR Scanner"

        setupPermission()
        codeScanner()
    }

    private fun codeScanner() {

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.apply {

            camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
            formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,

            autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
            scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
            isAutoFocusEnabled = true // Whether to enable auto focus or not
            isFlashEnabled = false // Whether to enable flash or not

            // Callbacks
            // Do what if scan successfully
            decodeCallback = DecodeCallback {
                runOnUiThread {
                    if(it.text.contains("UTAR - Universiti Tunku Abdul Rahman")) {
                        //Toast.makeText(baseContext, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                        val deviceConnection = Connection()
                        var dateTimeNow: String = if (android.os.Build.VERSION.SDK_INT >= 26) {
                            val dtf: DateTimeFormatter =
                                DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mma")
                            val myZone: ZoneId = ZoneId.of("Asia/Singapore")
                            val now: ZonedDateTime = ZonedDateTime.now(myZone)
                            dtf.format(now)
                        } else {
                            val formatter = SimpleDateFormat("dd-MM-yyyy hh:mma")
                            val date = Date()
                            formatter.format(date)
                        }
                        val filename = dateTimeNow.replace(" ", "_")

                        attendanceInfo = ""
                        //it.text.replace("/UTAR - Universiti Tunku Abdul Rahman", "")
                        val qrMessage = Scanner(it.text).useDelimiter("/")
                        val newLine = System.getProperty("line.separator")
                        while (qrMessage.hasNext()) {
                            attendanceInfo += qrMessage.next() + newLine
                        }
                        qrMessage.close()

                        if (deviceConnection.isInternetAvailable(applicationContext)) {
                            directory = getDir("History", MODE_PRIVATE)
                            val outputStream = FileOutputStream(File(directory, filename))
                            outputStream.write(attendanceInfo.toByteArray())
                        } else {
                            encryptedFile = CryptographyManager().getOrCreateEncryptedFile(
                                applicationContext, getDir("Pending", MODE_PRIVATE), filename)
                            encryptedFile.openFileOutput().apply {
                                write(attendanceInfo.toByteArray(StandardCharsets.UTF_8))
                                flush()
                                close()
                            }
                            /*directory = getDir("Pending", MODE_PRIVATE)
                            val outputStream = FileOutputStream(File(directory, filename))
                            outputStream.write(attendanceInfo.toByteArray())*/
                        }

                        findViewById<TextView>(R.id.scanResult).text = attendanceInfo
                        Toast.makeText(applicationContext, "Attendance taken!", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(applicationContext, getString(R.string.invalidQR), Toast.LENGTH_LONG).show()
                    }
                }
                errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                    runOnUiThread {
                        Toast.makeText(
                            baseContext, "Camera initialization error: ${it.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            scannerView.setOnClickListener {
                codeScanner.startPreview()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermission() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if(permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            // When requestCode == CAMERA_REQUEST_CODE
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "You need the camera permission to be able to use this app!",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Can add a button here in case user wants to grant the permission in the app immediately, rather than needing to restart the app
                } else {
                    //successfully get permission
                }
            }
        }
    }
}