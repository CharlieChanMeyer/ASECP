package com.thesis.asecp

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.nio.charset.Charset
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;


class RestaurantPresentation : AppCompatActivity() {

    //ImageView for the picture of restaurant
    private lateinit var restaurantPicture: ImageView
    //TextView for the information of restaurant
    private lateinit var textName: TextView
    private lateinit var textGrade: TextView
    private lateinit var textDescription: TextView
    private lateinit var textGPS: TextView

    //Button for returning to the menu
    private lateinit var menu: Button

    //get value of global variables
    private var globalVars = GlobalVariables.Companion

    //onCreate Function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Link to the layout file restaurant_presentation
        setContentView(R.layout.activity_restaurant_presentation)

        //Link the variable to the imageView and textView with id
        restaurantPicture = findViewById(R.id.Picture)
        textName = findViewById(R.id.Name)
        textGrade = findViewById(R.id.Grade)
        textDescription = findViewById(R.id.Description)
        textGPS = findViewById(R.id.GPS)

        //Link the variable to the button with the menuMapView id
        menu = findViewById(R.id.Menu)
        //add On click Listener to the button.
        //When the user click on the button, create a new intent linked to the Menu class and start the new intent,
        //then finish the actual intent.
        menu.setOnClickListener {
            var intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

        //Request the information of restaurant from sever
        postVolley(globalVars.globalRestaurantId)

    }

    // This functions calls the PHP files at the given url and sends the id of restaurant
    // to request to the server to get the information of a restaurant.
    private fun postVolley(id: Int) {
        val queue = Volley.newRequestQueue(this)
        //The url of phpAPI
        var url = globalVars.globalAPILink + "restaurant_present.php"
        //Request the information of restaurant with the id of restaurant
        val requestBody = "id=$id"
        //The request object
        val stringReq : StringRequest =
            object : StringRequest(
                //The object use the post method to the url
                Method.POST, url,
                //Add a response listener
                Response.Listener { response ->
                    //Replace the " to nothing, and split the response based on the  :
                    var strResponse = response.replace("\"", "").split(",", ":")

                    //If the first item from the split response is success
                    if ("success" in strResponse[1]) {
                        // The picture is at the position 11
                        val photo = strResponse[11].replace(" ", "")
                        // The name is at the position 5
                        val name = "Name: " + strResponse[5]//.replace("\"", "")
                        // The grade is at the position 9
                        val grade = "Grade: " + strResponse[9]//split(": ")[1].replace("\"", "")
                        // The description is at the position 7
                        val description = "Description: " + strResponse[7]//.split(": ")[1].replace("\"", "")
                        //The GPS is at the position 13
                        val gps = "GPS: " + strResponse[13] + "," + strResponse[14].replace("}", "")

                        //Load the picture from server and show
                        Glide.with(this).load(globalVars.globalAPILink+"uploads/"+photo).error(R.mipmap.ic_launcher_round).into(restaurantPicture)

                        //Present name in textView
                        textName.text = name
                        //Present description in textView
                        textDescription.text = description
                        //Present Grade in textView
                        textGrade.text = grade
                        //Present GPS in textView
                        textGPS.text = gps

                    } else {
                        Toast.makeText( this, strResponse[1], Toast. LENGTH_SHORT) .show()
                    }
                },
                // If we cannot get a response we display the error message
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