package com.example.runningtracker.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runningtracker.R
import com.example.runningtracker.adapter.RunAdapter
import com.example.runningtracker.databinding.FragmentRunBinding
import com.example.runningtracker.util.SortType
import com.example.runningtracker.util.TrackingUtility
import com.example.runningtracker.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class RunFragment : Fragment(){
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentRunBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private val permissionList = mutableListOf<String>()

    private lateinit var runAdapter: RunAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRunBinding.inflate(inflater,container,false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            if(!TrackingUtility.hasLocationPermission(requireContext())){
                Toast.makeText(requireContext(),"Permission denied",Toast.LENGTH_SHORT).show()
                AlertDialog.Builder(requireContext())
                    .setTitle("Permission Denied")
                    .setMessage("You can grant permission in settings")
                    .setPositiveButton("Ok") { _, _ ->
                        startActivity(
                            Intent(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context?.packageName,null )
                            )
                        )
                    }
                    .setNegativeButton("Cancel"){dialogInterface, _ ->
                        dialogInterface.cancel()
                    }
                    .show()
            }
        }
        requestPermission()
        setupRecyclerView()

        when(viewModel.sortType){
            SortType.DATE -> binding.spFilter.setSelection(0)
            SortType.RUNNING_TIME -> binding.spFilter.setSelection(1)
            SortType.DISTANCE -> binding.spFilter.setSelection(2)
            SortType.AVG_TIME -> binding.spFilter.setSelection(3)
            SortType.CALORIES -> binding.spFilter.setSelection(4)
        }

        binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                when(pos){
                    0 -> viewModel.sortRuns(SortType.DATE)
                    1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                    2 -> viewModel.sortRuns(SortType.DISTANCE)
                    3 -> viewModel.sortRuns(SortType.AVG_TIME)
                    4 -> viewModel.sortRuns(SortType.CALORIES)
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        viewModel.runs.observe(viewLifecycleOwner,{
            runAdapter.submitList(it)
        })
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }

    private fun setupRecyclerView() = binding.rvRuns.apply {
        runAdapter = RunAdapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }



    private fun requestPermission(){
        if(TrackingUtility.hasLocationPermission(requireContext())){
            return
        }else{
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                permissionList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                Timber.d("${permissionList[2]} is added now")
            }
            permissionLauncher.launch(permissionList.toTypedArray())
        }

    }



}