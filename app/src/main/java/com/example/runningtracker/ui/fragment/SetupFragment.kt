package com.example.runningtracker.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runningtracker.R
import com.example.runningtracker.databinding.ActivityMainBinding
import com.example.runningtracker.databinding.FragmentSetupBinding
import com.example.runningtracker.util.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningtracker.util.Constants.KEY_NAME
import com.example.runningtracker.util.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {
    private lateinit var binding: FragmentSetupBinding
    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    var isFirstOpen = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSetupBinding.inflate(inflater,
        container,
        false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isFirstOpen){
            val navOption = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment,true)
                .build()
            findNavController().navigate(R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOption
            )
        }

        binding.tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if(success)
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            else
                Snackbar.make(requireView(),"Please enter all fields",Snackbar.LENGTH_SHORT).show()
        }
    }


    private fun writePersonalDataToSharedPref(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        if(name.isEmpty() || weight.isEmpty()){
            return false
        }

        sharedPref.edit()
            .putString(KEY_NAME,name)
            .putFloat(KEY_WEIGHT,weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE,false)
            .apply()

        return true
    }
}