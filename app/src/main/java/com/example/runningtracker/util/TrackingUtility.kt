package com.example.runningtracker.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.example.runningtracker.services.Polyline
import com.vmadalin.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

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
            ) == PackageManager.PERMISSION_GRANTED //&&
//            ContextCompat.checkSelfPermission(context,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
        }


    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String{
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /= 10
        if(includeMillis){
            return "${if(hours < 10) "0" else ""}$hours:"+
                    "${if(minutes < 10) "0" else ""}$minutes:"+
                    "${if(seconds < 10) "0" else ""}$seconds:"+
                    "${if(milliseconds < 10) "0" else ""}$milliseconds"
        }
        return "${if(hours < 10) "0" else ""}$hours:"+
                "${if(minutes < 10) "0" else ""}$minutes:"+
                "${if(seconds < 10) "0" else ""}$seconds"
    }

    fun calculatePolylineLength(polyline: Polyline): Float{
        var distance =0f
        for(i in 0..polyline.size-2){
            val pos1 = polyline[i]
            val pos2 = polyline[i + 1]
            var result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }

}