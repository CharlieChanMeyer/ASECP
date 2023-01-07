package com.thesis.asecp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import java.lang.ref.WeakReference
import android.view.ViewGroup
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mapbox.maps.*
import com.mapbox.maps.plugin.gestures.*
import com.thesis.asecp.databinding.ItemCalloutViewBinding
import java.nio.charset.Charset


class Map : AppCompatActivity() {

    private lateinit var locationPermissionHelper: LocationPermissionHelper

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private lateinit var mapView: MapView

    //get value of global var
    private var globalVars = GlobalVariables.Companion

    private lateinit var viewAnnotationManager: ViewAnnotationManager
    private val viewAnnotationViews = mutableListOf<View>()

    lateinit var menuButton: Button

    private companion object {
        const val SELECTED_ADD_COEF_PX = 25
        const val STARTUP_TEXT = "Click on a map to add a view annotation."
        const val ADD_VIEW_ANNOTATION_TEXT = "Add view annotations to re-frame map camera"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        menuButton = findViewById(R.id.menuMapView)
        menuButton.setOnClickListener {
            var intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

        mapView = findViewById(R.id.mapView)
        viewAnnotationManager = mapView.viewAnnotationManager

        setAnnotations()

        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }
    }

    private fun onMapReady() {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()
            setupGesturesListener()
        }
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
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
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(this, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setAnnotations() {
        val queue = Volley.newRequestQueue(this)
        var url = globalVars.globalAPILink+"getRestaurantsAnnotation.php"

        val requestBody = ""
        val stringReq : StringRequest =
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    var arrayResponse = response.split("|")
                    Log.e("DEBUG",arrayResponse.toString())
                    if (arrayResponse[0] == "success") {
                        arrayResponse = arrayResponse.drop(1)
                        Log.e("DEBUG",arrayResponse.toString())
                        for (restaurant in arrayResponse) {
                            var tmp = restaurant.split("/")
                            var id = tmp[0].toInt()
                            var name = tmp[1]
                            var coordsRestaurant = tmp[2].split(", ")
                            var point = Point.fromLngLat(coordsRestaurant[1].toDouble(),coordsRestaurant[0].toDouble())
                            addViewAnnotation(id,name,point)
                        }
                    } else {
                        Log.e("Error",arrayResponse[3])
                    }
                },
                Response.ErrorListener { error ->
                    var strError = error.toString()
                    Log.e("Error",strError)
                }
            ){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }

    @SuppressLint("SetTextI18n")
    private fun addViewAnnotation(id:Int,name:String,GPS:Point) {
        val viewAnnotation = viewAnnotationManager.addViewAnnotation(
            resId = R.layout.item_callout_view,
            options = viewAnnotationOptions {
                geometry(GPS)
                allowOverlap(true)
            }
        )
        viewAnnotationViews.add(viewAnnotation)
        ItemCalloutViewBinding.bind(viewAnnotation).apply {
            textNativeView.text = name
            closeNativeView.setOnClickListener {
                viewAnnotationManager.removeViewAnnotation(viewAnnotation)
                viewAnnotationViews.remove(viewAnnotation)
            }
            selectButton.setOnClickListener {
                Toast.makeText(this@Map, id.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}