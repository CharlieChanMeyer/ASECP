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

    lateinit var textInputEditTextName: EditText
    lateinit var textInputEditTextEmail: EditText
    lateinit var textInputEditTextPassword: EditText
    lateinit var buttonSubmit: Button
    lateinit var buttonLogin: Button

    lateinit var email: String
    lateinit var password: String

    lateinit var textViewError: TextView

    //get value of global var
    private var globalVars = GlobalVariables.Companion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        textInputEditTextEmail = findViewById(R.id.RegiEmailInput)
        textInputEditTextPassword = findViewById(R.id.RegiPasswordInput)
        buttonSubmit = findViewById(R.id.regiRegisterButton)
        buttonLogin = findViewById(R.id.regiLoginButton)
        textViewError = findViewById(R.id.regiErrorLogin)

        buttonSubmit.setOnClickListener {
            email = textInputEditTextEmail.text.toString()
            password = textInputEditTextPassword.text.toString()

            postVolley(email, password)

        }

        buttonLogin.setOnClickListener {
            var intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun postVolley(email: String, password: String) {
        val queue = Volley.newRequestQueue(this)
        var url = globalVars.globalAPILink+"register.php"

        val requestBody = "email=$email&password=$password"
        val stringReq : StringRequest =
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    // response
                    var strResp = response.toString()
                    if ("success" in strResp) {
                        var intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        textViewError.text = strResp
                    }
                },
                Response.ErrorListener { error ->
                    var strError = error.toString()
                    textViewError.text = strError
                }
            ){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }
}