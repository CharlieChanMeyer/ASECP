package com.thesis.asecp

import android.content.ContentValues.TAG
import android.content.Intent
import android.nfc.Tag
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

    override fun onCreate(savedInstanceState: Bundle?) {
        //Save Instance state, and link the activity to the menu layout
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        //Link the button to the view object
        logoutButton = findViewById(R.id.logout)
        //On click on logout, call the logout function
        logoutButton.setOnClickListener{
            logout()
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






