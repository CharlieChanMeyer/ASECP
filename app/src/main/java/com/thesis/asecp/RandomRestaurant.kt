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

    private lateinit var randomButton: Button
    lateinit var restaurantDescriptionText: TextView
    private lateinit var locationManager: LocationManager
    private var globalVars = GlobalVariables.Companion
    private val locationPermissionCode = 2
    private lateinit var tvGpsLocation: TextView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var restaurant_name: TextView
    private lateinit var restaurant_grade: TextView
    private lateinit var menuButton: Button
    private lateinit var restaurantImage: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_restaurant)

        //randomButton = findViewById(R.id.button)
        restaurantDescriptionText = findViewById(R.id.restaurantDescriptionText)
        restaurantImage = findViewById(R.id.restaurantImage)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        restaurant_grade = findViewById(R.id.restaurant_grade)
        restaurant_name = findViewById(R.id.restaurant_name)
        menuButton = findViewById(R.id.menuRandomView)

        menuButton.setOnClickListener {
            var intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

        var location = getCurrentLocation()


        if (location == null){
            Toast.makeText(this,"Can't get location, default location at I-Wing", Toast.LENGTH_SHORT).show()

            location = Location(LocationManager.GPS_PROVIDER)
            location.latitude = 34.546472
            location.longitude = 135.506644
        }
        else{
            postVolley(location)
        }
    }

    private fun getCurrentLocation():Location? {
        var funlocation:Location? = null
        var check = 0
        if (checkPermissions()){
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
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task ->
                    val location: Location? = task.result
                    if(location == null){
                        Toast.makeText(this,"Null Received",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "Get Success", Toast.LENGTH_SHORT).show()
                        //restaurant_name.text = location.latitude.toString()
                        //restaurant_grade.text = location.longitude.toString()
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

    private fun isLocationEnabled(): Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermissions(): Boolean{
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_REQUEST_ACCESS_LOCATION){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else{
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun postVolley(location: Location){
        val queue = Volley.newRequestQueue(this)
        val url = globalVars.globalAPILink+"random_restaurant.php"

        var tmp = location.latitude.toString() + "," + location.longitude.toString()

        val requestBody = "pos=$tmp"
        val stringReq : StringRequest =
            @SuppressLint("UseCompatLoadingForDrawables")
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    // response

                    var strResp = response.split(",", "|").toMutableList()
                    if ("success" in strResp[0]) {

                        val restaurantId = strResp[8].split(": ")[1].replace("\"", "")

                        var intent = Intent(this, RestaurantPresentation::class.java)
                        startActivity(intent)
                        finish()

                       /* val urlPhoto = strResp[7].split("\"")[3]
                        val description = strResp[5].split(": ")[1].replace("\"", "")
                        val restaurantName = strResp[2].split(": ")[1].replace("\"", "")
                        val restaurantGrade = strResp[6].split(": ")[1].replace("\"", "")

                        Glide.with(this).load(globalVars.globalAPILink+"uploads/"+urlPhoto).into(restaurantImage)
                        restaurantDescriptionText.text = description
                        restaurant_name.text = restaurantName
                        restaurant_grade.text = "Grade : "+restaurantGrade+"/5"*/


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