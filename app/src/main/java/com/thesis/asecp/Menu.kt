package com.thesis.asecp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.thesis.asecp.R

class Menu : AppCompatActivity() {

    private lateinit var randomButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        randomButton = findViewById(R.id.randomButton)

        randomButton.setOnClickListener {
            var intent = Intent(this, RandomRestaurant::class.java)
            startActivity(intent)
            finish()
        }

    }
}






