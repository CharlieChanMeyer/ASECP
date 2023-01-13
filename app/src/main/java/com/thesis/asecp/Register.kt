package com.thesis.asecp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.nio.charset.Charset

class Register : AppCompatActivity() {

    //Input variable
    lateinit var textInputEditTextEmail: EditText
    lateinit var textInputEditTextPassword: EditText

    //Button variable
    lateinit var buttonSubmit: Button
    lateinit var buttonLogin: Button

    //email and password variable
    lateinit var email: String
    lateinit var password: String

    //Error display variable
    lateinit var textViewError: TextView

    //get value of global var
    private var globalVars = GlobalVariables.Companion

    override fun onCreate(savedInstanceState: Bundle?) {
        //Create the saved instance state and link the activity to the register layout
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Link the variable to their object in the layout
        textInputEditTextEmail = findViewById(R.id.RegiEmailInput)
        textInputEditTextPassword = findViewById(R.id.RegiPasswordInput)
        buttonSubmit = findViewById(R.id.regiRegisterButton)
        buttonLogin = findViewById(R.id.regiLoginButton)
        textViewError = findViewById(R.id.regiErrorLogin)

        //On click on sumbit, get the email and password, and pass it to the postVolley function
        buttonSubmit.setOnClickListener {
            email = textInputEditTextEmail.text.toString()
            password = textInputEditTextPassword.text.toString()

            postVolley(email, password)
        }

        //On click on login, create the login intent, start it and finish the register intent
        buttonLogin.setOnClickListener {
            var intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

    }

    //postVolley function that register the user
    //Parameters: email --> String
    //            password --> String
    private fun postVolley(email: String, password: String) {
        //Create a queue with this context
        val queue = Volley.newRequestQueue(this)
        //create a local variable for the api url
        var url = globalVars.globalAPILink+"register.php"

        //pass in argument the email and password
        val requestBody = "email=$email&password=$password"
        //create a string request object
        val stringReq : StringRequest =
            object : StringRequest(
                //Declare the type of method (Post) and the url
                Method.POST, url,
                //On response listener
                Response.Listener { response ->
                    // response
                    var strResp = response.toString()
                    //if the response is success, create the login intent, start it and finish the register intent
                    if ("success" in strResp) {
                        var intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        finish()
                    //Else, show the error in the error display
                    } else {
                        textViewError.text = strResp
                    }
                },
                //On error listener, show the error in the error display
                Response.ErrorListener { error ->
                    var strError = error.toString()
                    textViewError.text = strError
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