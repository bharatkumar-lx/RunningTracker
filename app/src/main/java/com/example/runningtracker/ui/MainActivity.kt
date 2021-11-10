package com.example.runningtracker.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.example.runningtracker.R
import com.example.runningtracker.databinding.ActivityMainBinding
import com.example.runningtracker.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupBottomNavigationBar()
        //if mainActivity destroyed but service running
        navigateToTrackingFragmentIfNeeded(intent)
        }

    private fun setupBottomNavigationBar(){
        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.findNavController()
        Timber.d(navController.currentBackStackEntry.toString())
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.runFragment,R.id.statisticsFragment,R.id.settingsFragment ->
                    binding.bottomNavigationView.visibility = View.VISIBLE
                else -> binding.bottomNavigationView.visibility = View.GONE
            }
        }
        binding.bottomNavigationView.setupWithNavController(navController)
//        binding.bottomNavigationView.setOnItemSelectedListener {
//            navHostFragment.findNavController().popBackStack(it.itemId,true)
//            navHostFragment.findNavController().navigate(it.itemId)
//            true
//        }
        binding.bottomNavigationView.setOnItemReselectedListener { /*no operation*/ }
    }

    //run if mainActivity still running
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    //direct mainActivity to tracking activity
    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT){
            navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Timber.d("back button pressed")
    }

}

