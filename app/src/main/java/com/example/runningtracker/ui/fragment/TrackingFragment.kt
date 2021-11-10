package com.example.runningtracker.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentTrackingBinding
import com.example.runningtracker.db.Run
import com.example.runningtracker.services.Polyline
import com.example.runningtracker.services.TrackingService
import com.example.runningtracker.util.Constants.ACTION_PAUSE_SERVICE
import com.example.runningtracker.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningtracker.util.Constants.ACTION_STOP_SERVICE
import com.example.runningtracker.util.Constants.MAP_ZOOM
import com.example.runningtracker.util.Constants.POLYLINE_COLOR
import com.example.runningtracker.util.Constants.POLYLINE_WIDTH
import com.example.runningtracker.util.TrackingUtility
import com.example.runningtracker.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.math.round


@AndroidEntryPoint
class TrackingFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentTrackingBinding
    private var map: GoogleMap? = null

    private var currentTimeInMills = 0L
    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    @set:Inject
    var weight = 80f

    private var menu: Menu? =null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackingBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync{
            map = it
            addAllPolylines()
        }
        binding.btnToggleRun.setOnClickListener {
           toggleRun()
        }
        binding.btnFinishRun.setOnClickListener{
            zoomToSeeWholeTrack()
            endRunAndSaveToDb()
        }
        subscribeToObservers()
    }

    //send intent to define action of service
    private fun sendCommandToService(action: String){
        Intent(requireContext(),TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size -2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner,{
            updateTracking(it)
        })
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner,{
            currentTimeInMills = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(it,true)
            binding.tvTimer.text = formattedTime
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner,{
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu,menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(currentTimeInMills > 0){
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.cancelTracking ->{
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCancelTrackingDialog(){
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cancel the Run?")
            .setMessage("Are you sure to cancel the run")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes"){_,_ ->
                stopRun()
            }
            .setNegativeButton("No"){ dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }

    private fun stopRun(){
        binding.btnFinishRun.visibility = View.INVISIBLE
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun toggleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }



    private fun updateTracking(isTracking: Boolean){
        this.isTracking = isTracking
        if(!isTracking){
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.visibility = View.VISIBLE
        }else{
            binding.btnToggleRun.text = "Stop"
            menu?.getItem(0)?.isVisible = true
            binding.btnFinishRun.visibility = View.GONE
        }
    }

    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(),
                    MAP_ZOOM)
            )
        }
    }

    private fun addAllPolylines(){
        for(polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun zoomToSeeWholeTrack(){
        val bounds = LatLngBounds.builder()
        for (polyline in pathPoints){
            for(pos in polyline){
                bounds.include(pos)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.height* 0.05f).toInt())
        )
    }

    private fun endRunAndSaveToDb(){
        map?.snapshot { bmp ->
            var distanceInMeter = 0
            for(polyline in pathPoints){
                distanceInMeter += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = round((distanceInMeter/1000f) /(currentTimeInMills/ 1000f / 60 / 60) *10) / 10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeter/1000f)*weight).toInt()
            val run = Run(bmp,dateTimeStamp,avgSpeed,distanceInMeter,currentTimeInMills,caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.runFragment),
                "Run saved Successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()

        }

    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

}