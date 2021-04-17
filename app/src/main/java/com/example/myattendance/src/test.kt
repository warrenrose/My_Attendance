package com.example.myattendance.src

import android.R
import android.app.ListActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast


class test : ListActivity() {
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        val values = arrayOf(
            "Android", "iPhone", "WindowsMobile",
            "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
            "Linux", "OS/2"
        )
        val adapter = ArrayAdapter(
            this,
            R.layout.simple_list_item_1, values
        )
        listAdapter = adapter
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        val item = listAdapter.getItem(position) as String
        Toast.makeText(this, "$item selected", Toast.LENGTH_LONG).show()
    }
}