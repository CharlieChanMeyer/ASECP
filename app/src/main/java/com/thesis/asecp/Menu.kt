package com.thesis.asecp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.thesis.asecp.R
import java.nio.charset.Charset

class Menu : AppCompatActivity() {

    //Logout button
    private lateinit var logoutButton: Button

    //get value of global var
    private var globalVars = GlobalVariables.Companion
    //random button variable
    private lateinit var randomButton: Button
    //map button variable
    lateinit var mapButton: Button
    //add restaurant button variable
    lateinit var buttonAddRest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        //Save Instance state, and link the activity to the menu layout
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        //Link the button to the view object
        logoutButton = findViewById(R.id.logout)
        //On click on logout, call the logout function
        logoutButton.setOnClickListener {
            logout()
        }

        buttonAddRest = findViewById(R.id.addRestButton)

        buttonAddRest.setOnClickListener {
            Log.e("Test", "Message")
            var intent = Intent(this, AddRestaurant::class.java)
            startActivity(intent)
            finish()
        }

        //Link the button to the view object
        mapButton = findViewById(R.id.mapButton)
        //On click on map, create the Map intent, start it and finish the menu intent
        mapButton.setOnClickListener {
            var intent = Intent(this,com.thesis.asecp.Map::class.java)
            startActivity(intent)
            finish()
        }
        //Link the button to the view object
        randomButton = findViewById(R.id.randomButton)
        //On click on random restaurant, create the random restaurant intent, start it and finish the menu intent
        randomButton.setOnClickListener {
            var intent = Intent(this, RandomRestaurant::class.java)
            startActivity(intent)
            finish()
        }
    }

    //Logout function that logout a user
    private fun logout() {
        //Create a queue with this context
        val queue = Volley.newRequestQueue(this)
        //create a local variable for the api url
        var url = globalVars.globalAPILink+"logout.php"
        //Get the email and apiKey from the global Variable
        var email = globalVars.globalUserEmail
        var apiKey = globalVars.globalUserApiKEY

        //pass in argument the email and apiKey
        val requestBody = "email=$email&apiKey=$apiKey"
        //create a string request object
        val stringReq : StringRequest =
            object : StringRequest(
                //Declare the type of method (Post) and the url
                Method.POST, url,
                //On response listener
                Response.Listener { response ->
                    //If response is success, create the Login intent, start it and finish the menu intent
                    if (response == "success") {
                        var intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        finish()
                        //Else, log the response
                    } else {
                        Log.e(TAG,response)
                    }
                },
                //On error listener, log the error
                Response.ErrorListener { error ->
                    Log.e(TAG,error.toString())
                }
            ){
                //Set the default charset
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        //Add the string request in the queue
        queue.add(stringReq)
    }

}






