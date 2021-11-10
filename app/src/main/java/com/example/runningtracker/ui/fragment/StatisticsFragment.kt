package com.example.runningtracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runningtracker.databinding.FragmentStatisticsBinding
import com.example.runningtracker.util.TrackingUtility
import com.example.runningtracker.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round


@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }


    private fun subscribeToObservers(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner,{
            it?.let {
                binding.tvTotalTime.text = TrackingUtility.getFormattedStopWatchTime(it)
            }
        })

        viewModel.totalDistance.observe(viewLifecycleOwner,{
            it?.let {
                val km = it/1000f
                val totalDistance = round(km*10f)/10f
                binding.tvTotalDistance.text = "${totalDistance}km"
            }
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner,{
            it?.let {
                val avgSpeed = round(it*10f)/10f
                binding.tvAverageSpeed.text = "${avgSpeed}km/h"
            }
        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner,{
            it?.let {
                binding.tvTotalCalories.text = "${it}kcal"
            }
        })

    }

}