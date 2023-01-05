package com.thesis.asecp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import java.nio.charset.Charset
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.thesis.asecp.GlobalVariables.Companion.PERMISSION_REQUEST_ACCESS_LOCATION

class RandomRestaurant : AppCompatActivity() {
    //create the buttons in the class variable

    private lateinit var randomButton: Button
    lateinit var restaurantDescriptionText: TextView
    lateinit var restaurantImage: ImageView
    private lateinit var locationManager: LocationManager
    private var globalVars = GlobalVariables.Companion
    private val locationPermissionCode = 2
    private lateinit var tvGpsLocation: TextView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var menuButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_restaurant)

        //randomButton = findViewById(R.id.button)
        //restaurantDescriptionText = findViewById(R.id.textView)
        //restaurantImage = findViewById(R.id.imageView)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        tvLatitude = findViewById(R.id.tv_latitude)
        tvLongitude = findViewById(R.id.tv_longitude)

        menuButton = findViewById(R.id.menuRandomView)
        menuButton.setOnClickListener {
            var intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

        var location = getCurrentLocation()

        if (location == null){
            Toast.makeText(this,"Can't get location", Toast.LENGTH_SHORT).show()
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
                        tvLatitude.text = location.latitude.toString()
                        tvLongitude.text = location.longitude.toString()
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
                    Log.e("response", response.toString())
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
                            tableRow.addView(tViewName);
                            strResp.removeAt(0)
                            strResp.removeAt(0)
                            tViewDate.text = strResp[0]
                            tViewDate.setTextColor(Color.parseColor("#000000"))
                            tViewDate.gravity = Gravity.CENTER;
                            tableRow.addView(tViewDate);
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