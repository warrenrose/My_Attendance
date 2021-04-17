package com.example.myattendance.src

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.myattendance.R

class History : AppCompatActivity() {
    public override fun onCreate(attendance: Bundle?) {
        super.onCreate(attendance)
        setContentView(R.layout.history)
        title = "History"

        val allAttendance = getAllAttendance()
        val listView = findViewById<ListView>(R.id.list_history)
        val adapter: ArrayAdapter<String> = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                allAttendance
        )
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // Send attendance file name to HistoryDetails
            val item = listView.adapter.getItem(position) as String
            val intent = Intent(this, HistoryDetails::class.java)
            intent.putExtra("filename", item)
            startActivity(intent)
        }

    }

    private fun getAllAttendance(): MutableList<String> {
        val attendanceList: MutableList<String> = ArrayList()
        val attendance = getDir("History", MODE_PRIVATE)
        for (att in attendance.listFiles()) {
            attendanceList.add(att.name)
        }
        return attendanceList
    }
}