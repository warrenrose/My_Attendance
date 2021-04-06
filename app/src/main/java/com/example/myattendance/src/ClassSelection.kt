package com.example.myattendance.src

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myattendance.R


class ClassSelection : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.class_selection)

        val class1 = findViewById<Button>(R.id.class_1)
        val class2 = findViewById<Button>(R.id.class_2)
        val class3 = findViewById<Button>(R.id.class_3)
        val class4 = findViewById<Button>(R.id.class_4)
        val class5 = findViewById<Button>(R.id.class_5)

        listOf(class1, class2, class3, class4, class5).forEach {
            it.setOnClickListener(::handleButtonClick)
        }
    }

    private fun handleButtonClick(view: View) {
        with(view as Button) {
            val intent = Intent(
                    baseContext, QrGenerator::class.java)
            intent.putExtra("class", this.text.toString())
            startActivity(intent)
        }
    }

    /*fun generateQR(view: View) {
        val intent = Intent(this, QrGenerator::class.java)
        intent.putExtra(EXTRA_MESSAGE,  )
        startActivity(intent)
    }*/
}
