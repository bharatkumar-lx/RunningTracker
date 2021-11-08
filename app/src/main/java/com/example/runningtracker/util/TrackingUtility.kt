package com.example.runningtracker.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.vmadalin.easypermissions.EasyPermissions

object TrackingUtility {

    fun hasLocationPermission(context: Context) =
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
           ContextCompat.checkSelfPermission(context,
           Manifest.permission.ACCESS_COARSE_LOCATION
               ) == PackageManager.PERMISSION_GRANTED &&
           ContextCompat.checkSelfPermission(context,
               Manifest.permission.ACCESS_FINE_LOCATION
           ) == PackageManager.PERMISSION_GRANTED

        }else{
            ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

}