package com.thesis.asecp

import android.app.Application

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

        const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
        const val PERMISSION_REQUEST_READ_FOLDERS = 1
    }
}