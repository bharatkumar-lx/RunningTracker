package com.example.runningtracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.runningtracker.databinding.ItemRunBinding
import com.example.runningtracker.db.Run
import com.example.runningtracker.util.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {
    inner class RunViewHolder(val binding: ItemRunBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id  == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val binding = ItemRunBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RunViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.binding.apply {
            Glide.with(ivRunImage).load(run.img).into(ivRunImage)
            val calender = Calendar.getInstance().apply {
                timeInMillis = run.timeInMillis
            }
            val dateFormat = SimpleDateFormat("dd/MM/yy",Locale.getDefault())
            tvDate.text = dateFormat.format(calender.time)

            tvAvgSpeed.text = "${run.avgSpeedInKMH}km/h"
            tvDistance.text = "${run.distanceInMeter/1000f}km"
            tvTime.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)
            tvCalories.text = "${run.caloriesBurned}kcal"
        }


    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}