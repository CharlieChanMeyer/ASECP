package com.thesis.asecp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.thesis.asecp.R

class Menu : AppCompatActivity() {

    lateinit var buttonAddRest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        buttonAddRest = findViewById(R.id.addRestButton)

        buttonAddRest.setOnClickListener {
            Log.e("Test","Message")
            var intent = Intent(this, AddRestaurant::class.java)
            startActivity(intent)
            finish()
        }
    }

}






