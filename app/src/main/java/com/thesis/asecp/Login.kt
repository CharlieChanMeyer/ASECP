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
    lateinit var emailText: EditText
    lateinit var passwordText: EditText

    lateinit var errorText: TextView

    //get value of global var
    private var globalVars = GlobalVariables.Companion

    lateinit var buttonLogin: Button
    lateinit var buttonRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

<<<<<<< HEAD:app/src/main/java/com/thesis/asecp/Login.kt

=======
        emailText = findViewById(R.id.emailInput)
        passwordText = findViewById(R.id.editTextTextPassword)

        errorText = findViewById(R.id.errorLogin)

        buttonLogin = findViewById(R.id.loginButton)
        buttonRegister = findViewById(R.id.registerButton)

        buttonLogin.setOnClickListener {

            postVolley(emailText.text.toString(),passwordText.text.toString())
        }

        buttonRegister.setOnClickListener {
            var intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }
>>>>>>> 341021869b9b69527b0941172ba4d1dac6c27b32:app/src/main/java/com/thesis/asecp/login.kt
    }

    private fun postVolley(email: String, password: String) {
        val queue = Volley.newRequestQueue(this)
        var url = globalVars.globalAPILink+"login.php"

        val requestBody = "email=$email&password=$password"
        val stringReq : StringRequest =
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    var arrayResponse = response.replace("\"","").split(",", ": ")
                    if (arrayResponse[1] == "success") {
                        globalVars.globalUserID = arrayResponse[5].toInt()
                        globalVars.globalUserEmail = arrayResponse[9]
                        var apiKey = arrayResponse[11].replace("}","")
                        apiKey = apiKey.replace("\n","")
                        globalVars.globalUserApiKEY = apiKey
                        var intent = Intent(this, Menu::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        errorText.text = arrayResponse[3]
                    }
                },
                Response.ErrorListener { error ->
                    var strError = error.toString()
                    errorText.text = strError
                }
            ){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }

}