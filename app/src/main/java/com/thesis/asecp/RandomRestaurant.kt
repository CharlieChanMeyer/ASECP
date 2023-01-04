package com.thesis.asecp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.nio.charset.Charset

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
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    // response
                    var strResp = response.split(",", "|").toMutableList()
                    if ("success" in strResp) {
                        var nbRestaurant = (strResp.size)/4 - 1
                        strResp.removeAt(0)
                        strResp.removeAt(0)
                        for (i in 0..nbRestaurant) {
                            val tableRow = TableRow(this)
                            val tViewName = TextView(this)
                            var tViewDate = TextView(this)
                            strResp.removeAt(0)
                            var tmp = strResp[0].split("/")
                            if (globalVars.globalLangAPP == "jp") {
                                tViewName.text = tmp[0]
                            } else {
                                tViewName.text = tmp[1]
                            }
                            tViewName.setTextColor(Color.parseColor("#000000"))
                            tViewName.gravity = Gravity.CENTER;
                            tViewName.background = resources.getDrawable(R.drawable.border)
                            tableRow.addView(tViewName);
                            strResp.removeAt(0)
                            strResp.removeAt(0)
                            tViewDate.text = strResp[0]
                            tViewDate.background = resources.getDrawable(R.drawable.border)
                            tViewDate.setTextColor(Color.parseColor("#000000"))
                            tViewDate.gravity = Gravity.CENTER;
                            tableRow.addView(tViewDate);
                            tableDisplay.addView(tableRow)
                            strResp.removeAt(0)
                        }
                    } else {
                        Toast.makeText(this, strResp[1], Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener { error ->
                    var strError = error.toString()
                    Toast.makeText(this, strError, Toast.LENGTH_SHORT).show()
                }
            ){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }
}