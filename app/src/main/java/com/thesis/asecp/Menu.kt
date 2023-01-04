package com.thesis.asecp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.thesis.asecp.R

class Menu : AppCompatActivity() {

    lateinit var buttonMap: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        buttonMap = findViewById(R.id.mapbutton)
        buttonMap.setOnClickListener {
            var intent = Intent(this, com.thesis.asecp.Map::class.java)
            startActivity(intent)
            finish()
        }

    }
}






