package com.thesis.asecp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.nio.charset.Charset

class Login : AppCompatActivity() {
    //email input variable
    lateinit var emailText: EditText
    //password input variable
    lateinit var passwordText: EditText
    //error display variable
    lateinit var errorText: TextView

    //get value of global var
    private var globalVars = GlobalVariables.Companion
    //login button variable
    lateinit var buttonLogin: Button
    //register button variable
    lateinit var buttonRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        //save instance state and link the activity to the login view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //link the input variable to the actual inputs in the view
        emailText = findViewById(R.id.emailInput)
        passwordText = findViewById(R.id.editTextTextPassword)
        //link the error display variable to the actual display
        errorText = findViewById(R.id.errorLogin)

        //link the buttons to their actual buttons
        buttonLogin = findViewById(R.id.loginButton)
        buttonRegister = findViewById(R.id.registerButton)

        //On click on login button, launch the postVolley function with the parameters from the email and password input
        buttonLogin.setOnClickListener {
            postVolley(emailText.text.toString(),passwordText.text.toString())
        }

        //On click on register button, create the register intent and start it, then finish this activity
        buttonRegister.setOnClickListener {
            var intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }
    }

    //postVolley function that verify if the user is authorized to login
    //Parameters: email --> String
    //            password --> String
    private fun postVolley(email: String, password: String) {
        //Create a queue with this context
        val queue = Volley.newRequestQueue(this)
        //create a local variable for the api url
        var url = globalVars.globalAPILink+"login.php"

        //pass in argument the email and password
        val requestBody = "email=$email&password=$password"
        //create a string request object
        val stringReq : StringRequest =
            object : StringRequest(
                //Declare the type of method (Post) and the url
                Method.POST, url,
                //On response listener
                Response.Listener { response ->
                    //Split the response and replace the characters "
                    var arrayResponse = response.replace("\"","").split(",", ": ")
                    //If the request is successful
                    if (arrayResponse[1] == "success") {
                        //set the userID and the email in the globalVariable
                        globalVars.globalUserID = arrayResponse[5].toInt()
                        globalVars.globalUserEmail = arrayResponse[9]
                        //Refract the apiKey
                        var apiKey = arrayResponse[11].replace("}","")
                        apiKey = apiKey.replace("\n","")
                        //Set the apiKey in the globalVariable
                        globalVars.globalUserApiKEY = apiKey
                        //Create the Menu intent, start it then finish the login intent
                        var intent = Intent(this, Menu::class.java)
                        startActivity(intent)
                        finish()
                    //Else, Show the error message in the error display
                    } else {
                        errorText.text = arrayResponse[3]
                    }
                },
                //On error listener, show the error message in the error display
                Response.ErrorListener { error ->
                    var strError = error.toString()
                    errorText.text = strError
                }
            ){
                //set the default charset
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        //Add the request in the queue
        queue.add(stringReq)
    }

}