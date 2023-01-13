package com.thesis.asecp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import java.nio.charset.Charset
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.thesis.asecp.GlobalVariables.Companion.PERMISSION_REQUEST_ACCESS_LOCATION

class RandomRestaurant : AppCompatActivity() {
    //create the buttons in the class variable

    //globalVars allows us to get access to the global variables shared by all activities
    private var globalVars = GlobalVariables.Companion

    //Variable type to call location functions
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //Menu Button to return to Menu
    private lateinit var menuButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_restaurant)


        //Section to return to Menu with button press
        menuButton.setOnClickListener {
            var intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }


        //Function to get User location
        var location = getCurrentLocation()

        //If the location comes back null then the default position is the I-Wing position
        if (location == null){
            Toast.makeText(this,"Can't get location, default location at I-Wing", Toast.LENGTH_SHORT).show()

            location = Location(LocationManager.GPS_PROVIDER)
            location.latitude = 34.546472
            location.longitude = 135.506644
        }

        //Function to get PHP/server response with input location
        postVolley(location)
    }

    //Function to get Current Location
    private fun getCurrentLocation():Location? {
        var funlocation:Location? = null
        //Check if the App has permissions to access Location
        if (checkPermissions()){
            //Check if the location is enabled on the device
            if (isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                }
                // Gets last locations of user
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task ->
                    val location: Location? = task.result
                    if(location == null){
                        // Display Null Received if location could not be obtained
                        Toast.makeText(this,"Null Received",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        // Display Get Success if location could be obtained
                        Toast.makeText(this, "Get Success", Toast.LENGTH_SHORT).show()
                        funlocation = location
                    }
                }
            }
            else{
                //settings open here
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else{
            requestPermission()
        }
        return(funlocation)
    }

    //Check if Location is Enabled
    private fun isLocationEnabled(): Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                )
    }

    //Requests Permissions to get Coarse and Fine location of the user
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    //Checks if user has permissions to access Coarse and Fine location
    private fun checkPermissions(): Boolean{
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
            return true
        }
        return false
    }

    //If the Permissions are given then the activity restarts if not a message pops up and says Denied
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_REQUEST_ACCESS_LOCATION){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                //Once we have Permissions call back getCurrentLocation function
                getCurrentLocation()
            }
            else{
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //This function makes a request to the server to get the information of a random restaurant
    // by calling the PHP files at the given url.
    //This functions then sends that information to the RestaurantPresentation activity
    private fun postVolley(location: Location){
        val queue = Volley.newRequestQueue(this)
        // URL where the PHP is located
        val url = globalVars.globalAPILink+"random_restaurant.php"

        //Setting up input variable
        var tmp = location.latitude.toString() + "," + location.longitude.toString()

        //We must make sure the name given in input must match the name inside the SQL database
        val requestBody = "pos=$tmp"
        val stringReq : StringRequest =
            @SuppressLint("UseCompatLoadingForDrawables")
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    // response

                    var strResp = response.split(",", "|").toMutableList()
                    //Checks if the response has a success message
                    if ("success" in strResp[0]) {
                        //Restaurant Id is positioned at the 8th place inside the response array
                        val restaurantId = strResp[8].split(": ")[1].replace("\"", "").toInt()

                        //Set the globalVariable globalRestaurantID to the restaurant's current ID
                        // for other activities to have access to it
                        globalVars.globalRestaurantID = restaurantId

                        //Start the RestaurantPresentation activity
                        var intent = Intent(this, RestaurantPresentation::class.java)
                        startActivity(intent)
                        finish()

                    }
                    // If the response contains a failure message we display the attached message
                    else {
                        Toast.makeText(this, strResp[1], Toast.LENGTH_SHORT).show()
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