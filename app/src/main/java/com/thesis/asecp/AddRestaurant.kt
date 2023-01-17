package com.thesis.asecp

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import com.thesis.asecp.GlobalVariables.Companion.PERMISSION_REQUEST_ACCESS_LOCATION
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.thesis.asecp.GlobalVariables.Companion.PERMISSION_REQUEST_READ_FOLDERS

class AddRestaurant : AppCompatActivity() {

    //variable's statements
    private lateinit var buttonBack : Button //Button to return to the menu
    private lateinit var nameInput : EditText //Restaurant's name
    private lateinit var gradeInput : RatingBar //Restaurant's rate
    private lateinit var gradeDisplayer : TextView //Restaurant's rate to diaplay
    private lateinit var longitudeInput : EditText //Restaurant's location : longitude
    private lateinit var latitudeInput : EditText //Restaurant's location : latitude
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var descriptionInput : EditText //Restaurant's description
    private lateinit var buttonAddPict: ImageView //Restaurant's picture
    private lateinit var buttonUpload: Button //Upload button

    private var globalVars = GlobalVariables.Companion
    private lateinit var imageUri: Uri
    private val postURL: String = globalVars.globalAPILink + "addrestaurant.php" //URL link to the server
    private lateinit var bitmap: Bitmap

    //Main method of AddRestaurant
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_picture)

        /*----------- Return button to the menu      -----------
        * setOnClickListener instructs the device to pay attention to the button Back.
        * If the cross button is pushed we go back to Menu.kt
        * */
        buttonBack = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            Log.e("Test","Message")
            var intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

        /*----------- Initialization of the variables      ------------------
        Associates to the variables the values of the corresponding inputs in activity_take_picture.xml
         */
        nameInput = findViewById(R.id.nameInput)
        gradeInput = findViewById(R.id.simpleRatingBar)
        gradeDisplayer = findViewById(R.id.rateDisplayer)
        gradeDisplayer.setText("Rate :2.5")
        gradeInput.setOnRatingBarChangeListener { ratingBar, fl, b ->
            gradeDisplayer.setText("Rate :"+fl)
        }
        longitudeInput = findViewById(R.id.longitudeInput)
        latitudeInput = findViewById(R.id.latitudeInput)

        /*-----------     Find location of the phone      ------------------*/
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        }
        var location = fusedLocationProviderClient.lastLocation //get the current location of the phone
        //Add a OnSuccessListerner to the lastLoc variable
        location.addOnSuccessListener {
            if (it == null){ //the location of the phone isn't available
                longitudeInput.setText("135.506644")
                latitudeInput.setText("34.546472")
                Toast.makeText(this,"Can't get location, location set to Nakamozu campus", Toast.LENGTH_SHORT).show()
            }
            else{
                /*The localization of the phone is success, so we directly fill in the GPS location box
                In this way, the user doesn't need to fill in this box*/
                longitudeInput.setText(it.longitude.toString())
                latitudeInput.setText(it.latitude.toString())
            }
        }
        //If the phone don't have any localisation sensor
        // set the camera location to Nakamozu campus with a zoom of 15.5, a pitch of 45 and a bearing of -17.6
        location.addOnFailureListener{
            longitudeInput.setText("135.506644")
            latitudeInput.setText("34.546472")
            Toast.makeText(this,"Can't get location, location set to Nakamozu campus", Toast.LENGTH_SHORT).show()
        }

        /*-----------     Description of the restaurant      ------------------*/
        descriptionInput = findViewById(R.id.descriptionInput)

        /*-----------     Upload an image of the restaurant      ------------------
        * When the user presses on the icon, the chooseFromGallery function is activated.*/
        buttonAddPict = findViewById(R.id.clickToUploadImg)
        buttonAddPict.setOnClickListener {
            chooseFromGallery()
        }

        /*-----------     Upload the restaurant      ------------------
        * When the user presses the upload button, the upload function is activated.*/
        buttonUpload = findViewById(R.id.uploadButton)
        buttonUpload.setOnClickListener {
            upload()
        }

    }

/*-----------     Request the permission to get phone's location      ------------------
* The permission is asked with ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION defined in AndroidManifest.xml
* ACCESS_COARSE_LOCATION allows the API to return the device's approximate location
* ACCESS_FINE_LOCATION allows the API to return the device's precise location
* */
private fun requestLocationPermission() {
    ActivityCompat.requestPermissions(
        this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
        PERMISSION_REQUEST_ACCESS_LOCATION
    )
}

/*-----------     Check the permission to get phone's location      ------------------
* If authorization is granted, the function returns True; otherwise, it returns False.
* */
private fun checkLocationPermissions(): Boolean{
    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        &&
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    {
        return true
    }
    return false
}

/*-----------     Request permission to use the gallery      ------------------
* The permission is asked with READ_EXTERNAL_STORAGE defined in AndroidManifest.xml
* READ_EXTERNAL_STORAGE allows the API to read files
* */
private fun requestImagePermission() {
    ActivityCompat.requestPermissions(
        this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
        PERMISSION_REQUEST_ACCESS_LOCATION
    )
}

/*-----------     Check the permission to use the gallery      ------------------
* If authorization is granted, the function returns True; otherwise, it returns False.
*
* */
private fun checkImagePermissions(): Boolean{
    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    {
        return true
    }
    return false
}

/*-----------     Select a picture from the gallery      ------------------*/
private fun chooseFromGallery() {

    if (checkImagePermissions()) { //Permission to read files is granted
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = "image/png"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes) //Add extended data to the intent
        resultLauncher.launch(intent.type) //Access to the gallery
    }
    else{
        //Permission to read files isn't granted, so request permission to read files
        Toast.makeText(this,"No access to gallery",Toast.LENGTH_SHORT).show()
        requestImagePermission()
    }
}

/*-----------     Launch the gallery and save the selected photo      ------------------*/
private val resultLauncher =
    registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            buttonAddPict.setImageURI(uri) //change the icon with the selected picture
            imageUri=uri //store the selected picture in the variable imageUri
        }
    }

/*-----------     Encode the selected picture in string      ------------------*/
fun encodeBitmapImage(uri: Uri): String? {
    val inputStream = contentResolver.openInputStream(uri)
    bitmap = BitmapFactory.decodeStream(inputStream)
    buttonAddPict.setImageBitmap(bitmap)
    var encodeImageString: String = "xxx"
    if(bitmap!=null) {
        var outPutStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 75, outPutStream) //compress the picture in PNG format
        var byteofimage = outPutStream.toByteArray()
        encodeImageString = Base64.encodeToString(byteofimage, Base64.DEFAULT) //encode the picture
    }
    else{
        Toast.makeText(this,"bitmap is empty",Toast.LENGTH_SHORT).show()
    }
    return encodeImageString
}

/*-----------     Upload everything to the server      ------------------*/
private fun upload(){
    //data to upload
    val name = nameInput.text.toString() //restaurant's name
    val grade = gradeInput.rating.toString() //restaurant's rate
    val gps = latitudeInput.text.toString()+", "+longitudeInput.text.toString() //restaurant's location
    val description = descriptionInput.text.toString() //restaurant's description

    val encodeImageString=encodeBitmapImage(imageUri)
    if (encodeImageString!="xxx"){
        val queue = Volley.newRequestQueue((this))
        var request: StringRequest =
            object : StringRequest(
                Method.POST, postURL,
                Response.Listener { response ->
                    if (response.toString().contains("Image : success | BDD : Success")) {
                        Toast.makeText(this,"upload successfully",Toast.LENGTH_SHORT).show()
                        var intent = Intent(this,Menu::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e(TAG,response.toString())
                        Toast.makeText(this,response,Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this,error.toString(),Toast.LENGTH_SHORT).show() //upload fail
                }
            ) {
                //To send everything at once, we group all the data into a hashmap.
                override fun getParams(): MutableMap<String, String>? {
                    var map = HashMap<String, String>()
                    map["name"] = name
                    map["grade"] = grade
                    map["description"] = description
                    if (encodeImageString != null) {
                        map["photo"] = encodeImageString
                    }
                    map["gps"] = gps
                    return map
                }
            }
        Log.e("Test Body",request.body.decodeToString())
        queue.add(request)
    }
}
}