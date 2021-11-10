package com.example.runningtracker.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.runningtracker.databinding.FragmentSettingsBinding
import com.example.runningtracker.util.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnApplyChanges.setOnClickListener {
            val success = applyChangesToSharedPref()
            if(success){
                Snackbar.make(view,"Saved Changes",Snackbar.LENGTH_LONG).show()
            }else{
                Snackbar.make(view,"Please fill fields",Snackbar.LENGTH_LONG).show()
            }
        }
    }
    private fun applyChangesToSharedPref(): Boolean {
        val nameText = binding.etName.text.toString()
        val weightText = binding.etWeight.text.toString()

        if(nameText.isEmpty() || weightText.isEmpty()){
            return false
        }
        sharedPref.edit()
            .putString(Constants.KEY_NAME,nameText)
            .putFloat(Constants.KEY_WEIGHT,weightText.toFloat())
            .apply()


        return true
    }


}