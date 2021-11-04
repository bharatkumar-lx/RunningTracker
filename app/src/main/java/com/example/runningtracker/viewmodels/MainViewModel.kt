package com.example.runningtracker.viewmodels

import androidx.lifecycle.ViewModel
import com.example.runningtracker.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
): ViewModel() {
}