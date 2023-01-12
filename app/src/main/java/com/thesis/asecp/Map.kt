package com.thesis.asecp

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.gestures.*
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.thesis.asecp.databinding.ItemCalloutViewBinding
import java.lang.ref.WeakReference
import java.nio.charset.Charset
import java.util.*


class Map : AppCompatActivity() {

    //Location permission helper object
    private lateinit var locationPermissionHelper: LocationPermissionHelper

    //Fused location provider object
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //Geocoder object
    private lateinit var geocoder: Geocoder

    //Listener object for indicator bearing
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    //Listener object for indicator position
    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    //Movement listener
    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    //Mapview object
    private lateinit var mapView: MapView

    //get value of global variables
    private var globalVars = GlobalVariables.Companion

    //view Annotation manager that stock the mapview
    private lateinit var viewAnnotationManager: ViewAnnotationManager
    private val viewAnnotationViews = mutableListOf<View>()

    //Button for returning to the menu
    lateinit var menuButton: Button

    //onCreate Function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Link to the layout file activity_map
        setContentView(R.layout.activity_map)

        //Link the variable to the button with the menuMapView id
        menuButton = findViewById(R.id.menuMapView)
        //add On click Listener to the button.
        //When the user click on the button, create a new intent linked to the Menu class and start the new intent, then finish the actual intent.
        menuButton.setOnClickListener {
            var intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

        //Link the fused location variable to the Location Services from the phone
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //Create a geocoder with this activity in context and the default locale from the phone
        geocoder = Geocoder(this, Locale.getDefault())
        //Link the mapView variable to the mapview with id mapview
        mapView = findViewById(R.id.mapView)
        //Link the viewAnnotation manager variable to the view annotation manager from the mapview
        viewAnnotationManager = mapView.viewAnnotationManager

        //Call the setAnnotations function
        setAnnotations()

        //If the permissions for getting the location are not ON, call the requestPermission function
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
        //Create a variable lastLoc that get the last location from the location provider
        var lastLoc = fusedLocationProviderClient.lastLocation

        //Add a OnSuccessListerner to the lastLoc variable
        lastLoc.addOnSuccessListener {
            //Get the address from the geocoder
            val address = geocoder.getFromLocation(it.latitude,it.longitude,1)
            //Get the countryCode
            val countryCode = address[0].countryCode

            //If the countryCode is JP, call the onMapReady function
            if (countryCode == "JP") {
                onMapReady()
            //Else
            } else {
                //Set the camera location to Nakamozu campus with a zoom of 15.5, a pitch of 45 and a bearing of -17.6
                val initialCameraOptions = CameraOptions.Builder()
                    .center(Point.fromLngLat(135.506644, 34.546472))
                    .pitch(45.0)
                    .zoom(15.5)
                    .bearing(-17.6)
                    .build()
                mapView.getMapboxMap().setCamera(initialCameraOptions)
            }
        }
        //If the phone don't have any localisation sensor
        // set the camera location to Nakamozu campus with a zoom of 15.5, a pitch of 45 and a bearing of -17.6
        lastLoc.addOnFailureListener{
            val initialCameraOptions = CameraOptions.Builder()
                .center(Point.fromLngLat(135.506644, 34.546472))
                .pitch(45.0)
                .zoom(15.5)
                .bearing(-17.6)
                .build()
            mapView.getMapboxMap().setCamera(initialCameraOptions)
        }

    }

    //onMapReady function
    private fun onMapReady() {
        //Set the zoom of the camera to 14.0
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        //Load the MAPBOX_STREETS style
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            //Launch the initialization of the components and setup the gesture listener
            initLocationComponent()
            setupGesturesListener()
        }
    }

    //requestPermission function that request access to the Location
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            GlobalVariables.PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    //setupGesturesListener Function that add onMoveListener to the mapView
    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    //initLocationComponent function
    private fun initLocationComponent() {
        //create a variable of the mapview location
        val locationComponentPlugin = mapView.location
        //update the settings of the location variable
        locationComponentPlugin.updateSettings {
            //enable it
            this.enabled = true
            //setup the locationPuck with the bearingImage, the shadowImage and the scaleExpression
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@Map,
                    com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_puck_icon
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@Map,
                    com.mapbox.maps.plugin.locationcomponent.R.drawable.mapbox_user_icon_shadow
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        //Add OnIndicatorPositionChangedListener and OnIndicatorBearingChangedListener
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    //onCameraTrackingDismissed that remove the listeners from the mapView
    private fun onCameraTrackingDismissed() {
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    //onDestroy function that remove the listeners to set free the sensors
    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    //onRequestPermissionResult that listen to the permission request and verify that the request was accepted
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //setAnnotation function: WEB API POST method to get information from the DB and process it
    private fun setAnnotations() {
        //Create a new request queue for this activity
        val queue = Volley.newRequestQueue(this)
        //Create the url variable from the global variable + the name of the file for this request
        var url = globalVars.globalAPILink+"getRestaurantsAnnotation.php"

        //This request don't need to send any variable to the php file
        val requestBody = ""
        //The request object
        val stringReq : StringRequest =
            object : StringRequest(
                //The object use the post method to the url
                Method.POST, url,
                //Add a response listener
                Response.Listener { response ->
                    //split the response based on the delimiter |
                    var arrayResponse = response.split("|")
                    //If the first item from the split response is success
                    if (arrayResponse[0] == "success") {
                        //drop the success item
                        arrayResponse = arrayResponse.drop(1)
                        //for every other items (restaurant)
                        for (restaurant in arrayResponse) {
                            //Split the string by the delimiter /
                            var tmp = restaurant.split("/")
                            //the id is at the position 0
                            var id = tmp[0].toInt()
                            //the name is at the position 1
                            var name = tmp[1]
                            //split the coordinates by the delimiter ", "
                            var coordsRestaurant = tmp[2].split(", ")
                            //Create a new point from the coordinates of the restaurant
                            var point = Point.fromLngLat(coordsRestaurant[1].toDouble(),coordsRestaurant[0].toDouble())
                            //call the addViewAnnotation function
                            addViewAnnotation(id,name,point)
                        }
                        //Else, send the error to the log
                    } else {
                        Log.e("Error",arrayResponse[3])
                    }
                },
                //Add ErrorListener that send the error to the log
                Response.ErrorListener { error ->
                    var strError = error.toString()
                    Log.e("Error",strError)
                }
            ){
                //Override the getBody function to set the default charset
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        //Add the request to the queue
        queue.add(stringReq)
    }

    @SuppressLint("SetTextI18n")
    //addViewAnnotation function
    //Parameters : id (Int) : id restaurant
    //             name (String) : name of the restaurant
    //             GPS (Point) : Location point of the restaurant
    private fun addViewAnnotation(id:Int,name:String,GPS:Point) {
        //Create a viewAnnotation from the item_callout_view at the GPS coordinates that can overlap another annotation
        val viewAnnotation = viewAnnotationManager.addViewAnnotation(
            resId = R.layout.item_callout_view,
            options = viewAnnotationOptions {
                geometry(GPS)
                allowOverlap(true)
            }
        )
        //Add the annotation
        viewAnnotationViews.add(viewAnnotation)
        //Bind the name of the restaurant to the annotation
        ItemCalloutViewBinding.bind(viewAnnotation).apply {
            textNativeView.text = name
            //Add a click listener so that the annotation can be closed by clicking on the cross
            closeNativeView.setOnClickListener {
                viewAnnotationManager.removeViewAnnotation(viewAnnotation)
                viewAnnotationViews.remove(viewAnnotation)
            }
            //add a onClickListener to the selectButton to send the user to the presentation page of the restaurant
            selectButton.setOnClickListener {
                Toast.makeText(this@Map, id.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}