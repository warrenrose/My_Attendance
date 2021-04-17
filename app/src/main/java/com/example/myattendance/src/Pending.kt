package com.example.myattendance.src

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.security.crypto.EncryptedFile
import com.example.myattendance.R
import com.example.myattendance.utility.Connection
import com.example.myattendance.utility.CryptographyManager
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Pending : AppCompatActivity() {
    private lateinit var uploadFile: StringBuilder
    private lateinit var encryptedFile: EncryptedFile

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pending)
        title = "Pending Attendance List"

        val allAttendance = getAllAttendance()
        val listView = findViewById<ListView>(R.id.list_pending)
        val adapter: ArrayAdapter<String> = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1, allAttendance
        )
        listView.adapter = adapter
        uploadFile = StringBuilder("")

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // Send attendance file name to HistoryDetails
            val item = listView.adapter.getItem(position) as String
            val intent = Intent(this, PendingDetails::class.java)
            intent.putExtra("filename", item)
            startActivity(intent)
        }

        findViewById<Button>(R.id.pending_submit).setOnClickListener {
            val allPending = getDir("Pending", MODE_PRIVATE)
            val historyDir = getDir("History", MODE_PRIVATE)
            val userConn = Connection()
            if (userConn.isInternetAvailable(applicationContext))  {
                if(allPending.listFiles().isNotEmpty()) {
                    for (att in allPending.listFiles()) {
                        encryptedFile = CryptographyManager().getOrCreateEncryptedFile(
                            applicationContext, allPending, att.name
                        )

                        uploadFile.append(readAttendance(encryptedFile))
                        //uploadFile.append(att.inputStream().readBytes().toString(Charsets.UTF_8))

                        val dtf: DateTimeFormatter =
                            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mma")
                        val filename = dtf.format(ZonedDateTime.now(ZoneId.of("Asia/Singapore"))).replace(" ", "_")

                        val outputStream = FileOutputStream(File(historyDir, filename))
                        outputStream.write(uploadFile.toString().toByteArray())
                    }
                    allPending.deleteRecursively()
                    Toast.makeText(this, getString(R.string.has_Connection), Toast.LENGTH_SHORT).show()
                    finish()
                    startActivity(intent)
                }
                else {
                    Snackbar.make(findViewById<ConstraintLayout>(R.id.constraintlayout), "No Files to Upload", Snackbar.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this, getString(R.string.no_Connection), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getAllAttendance(): MutableList<String> {
        val attendanceList: MutableList<String> = ArrayList()
        val attendance = getDir("Pending", MODE_PRIVATE)
        for (att in attendance.listFiles()) {
            attendanceList.add(att.name)
        }
        return attendanceList
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