package com.example.runningtracker.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runningtracker.R
import com.example.runningtracker.databinding.FragmentStatisticsBinding
import com.example.runningtracker.util.TrackingUtility
import com.example.runningtracker.viewmodels.StatisticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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


    private fun setBarChart(){
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            textColor = Color.BLACK

        }
        binding.barChart.axisLeft.apply {
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
        }
        binding.barChart.axisRight.apply {
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
        }

        binding.barChart.apply {
            description.text = "Avg Speed Over Time"
            legend.isEnabled = false
        }
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

        viewModel.runsSortedByDate.observe(viewLifecycleOwner,{
            it?.let {
                val allAvgSpeeds = it.indices.map { i ->
                    BarEntry(i.toFloat(),it[i].avgSpeedInKMH)
                }
                val barDataSet = BarDataSet(allAvgSpeeds,"Avg Speed").apply {
                    valueTextColor = Color.BLACK
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                }
                binding.barChart.data = BarData(barDataSet)
                binding.barChart.invalidate()
            }
        })

    }

}