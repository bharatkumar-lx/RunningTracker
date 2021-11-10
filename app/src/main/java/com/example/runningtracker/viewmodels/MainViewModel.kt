package com.example.runningtracker.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningtracker.db.Run
import com.example.runningtracker.repository.MainRepository
import com.example.runningtracker.util.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
): ViewModel() {

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }

    private val runsSortedByDate = mainRepository.getAllRunSortedByDate()
    private val runsSortedByDistance = mainRepository.getAllRunSortedByDistance()
    private val runsSortedByCalorieBurned = mainRepository.getAllRunSortedByCalorieBurned()
    private val runsSortedByTimeInMillis = mainRepository.getAllRunSortedByTimeInMillis()
    private val runsSortedByAvgSpeed = mainRepository.getAllRunSortedByAvgSpeed()

    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE

    init {
        runs.addSource(runsSortedByDate){ result ->
            if(sortType == SortType.DATE){
                result?.let { runs?.value =it }
            }
        }
        runs.addSource(runsSortedByDistance){ result ->
            if(sortType == SortType.DISTANCE){
                result?.let { runs?.value =it }
            }
        }
        runs.addSource(runsSortedByCalorieBurned){ result ->
            if(sortType == SortType.CALORIES){
                result?.let { runs?.value =it }
            }
        }
        runs.addSource(runsSortedByTimeInMillis){ result ->
            if(sortType == SortType.RUNNING_TIME){
                result?.let { runs?.value =it }
            }
        }
        runs.addSource(runsSortedByAvgSpeed){ result ->
            if(sortType == SortType.AVG_TIME){
                result?.let { runs?.value =it }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when(sortType) {
        SortType.DATE -> runsSortedByDate.value?.let { runs?.value = it }
        SortType.DISTANCE -> runsSortedByDistance.value?.let { runs?.value = it }
        SortType.CALORIES -> runsSortedByCalorieBurned.value?.let { runs?.value = it }
        SortType.RUNNING_TIME -> runsSortedByTimeInMillis.value?.let { runs?.value = it }
        SortType.AVG_TIME -> runsSortedByAvgSpeed.value?.let { runs?.value = it }
        else -> runsSortedByDate.value?.let { runs?.value = it }
    }.also {
        this.sortType = sortType
    }




}