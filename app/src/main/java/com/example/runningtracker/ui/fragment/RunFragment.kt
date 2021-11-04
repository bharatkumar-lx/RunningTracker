package com.example.runningtracker.ui.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runningtracker.databinding.FragmentRunBinding
import com.example.runningtracker.util.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.runningtracker.util.TrackingUtility
import com.example.runningtracker.viewmodels.MainViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment(),EasyPermissions.PermissionCallbacks{
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentRunBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRunBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission()
    }

    private fun requestPermission(){
        if(TrackingUtility.hasLocationPermission(requireContext())){
            return
        }else{
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
                EasyPermissions.requestPermissions(
                    this,
                    "Need Permissions to run",
                     REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }else{
                EasyPermissions.requestPermissions(
                    this,
                    "Need Permissions to run",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
     if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
         Log.d("RunActivity","inside permission denied permanently")
         SettingsDialog.Builder(requireContext()).build().show()
     }else{
         Log.d("RunActivity","else part request denied")
         requestPermission()
     }
    }


    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.d("RunActivity","permission granted")
    }

//    private fun permissionResult() =
//        ActivityCompat.OnRequestPermissionsResultCallback { requestCode, permissions, grantResults ->
//            Log.d("RunActivity","callback $requestCode")
//            EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
//        }


    //todo: need to do something about it
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }




}