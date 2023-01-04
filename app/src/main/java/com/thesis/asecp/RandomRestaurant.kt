package com.thesis.asecp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RandomRestaurant : AppCompatActivity() {
    //create the buttons in the class variable
    lateinit var menuButton: Button

    lateinit var tableDisplay: TableLayout

    private var globalVars = GlobalVariables.Companion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_restaurant)

    }

    fun postVolley(id: Int){
        val queue = Volley.newRequestQueue(this)
        val url = globalVars.globalAPILink+"random_restaurant.php"

        val requestBody = "user_id=$id"
        val stringReq : StringRequest =
            @SuppressLint("UseCompatLoadingForDrawables")
            object : StringRequest (
                Method.POST, url,
                Response.Listener { response ->
                    // response
                    var strResp = response.split(",", "|").toMutableList()
                    if ("sucess" in strResp) {
                        var nbRestaurant = (strResp.size)/4 - 1
                        strResp.removeAt(0)
                        strResp.removeAt(0)
                        for (i in 0 <= nbRestaurant){
                            val tableRow = TableRow(this)
                            val tViewName = TextView(this)
                            val tViewDate = TextView(this)
                            strResp.removeAt(0)
                            var tmp = strResp[0].split("/")
                            if (globalVars.globalLangAPP == "jp") {
                                tViewName.text = tmp[0]
                            } else {
                                tViewName.text = tmp[1]
                            }
                            tViewName.setTex
                        }
                    }
                }
                    )
    }
}