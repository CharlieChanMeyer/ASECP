package com.thesis.asecp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.thesis.asecp.R

class Menu : AppCompatActivity() {

    lateinit var mapButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        mapButton = findViewById(R.id.mapButton)
        mapButton.setOnClickListener {
            var intent = Intent(this,com.thesis.asecp.Map::class.java)
            startActivity(intent)
            finish()
        }
    }
}






