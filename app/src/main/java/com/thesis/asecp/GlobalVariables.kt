package com.thesis.asecp

import android.app.Application
import java.security.Permission

class GlobalVariables: Application() {
    companion object {
        /** Link for the API */
        var globalAPILink = "http://ec2-15-168-13-179.ap-northeast-3.compute.amazonaws.com/asecp/"

        /** User ID
         *  -1   : not logged
         *  else : User ID */
        var globalUserID = -1

        var globalUserApiKEY = ""

        var globalUserEmail = ""

        var globalRestaurantID = -1

        const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }
}