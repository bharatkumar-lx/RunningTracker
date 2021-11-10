package com.example.runningtracker.repository

import com.example.runningtracker.db.Run
import com.example.runningtracker.db.RunDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
     val runDao : RunDAO
){
    suspend fun insertRun(run: Run)  = runDao.insertRun(run)

    suspend fun delete(run: Run)  = runDao.insertRun(run)

    fun getAllRunSortedByDate() = runDao.getAllRunsSortedByDate()

    fun getAllRunSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()

    fun getAllRunSortedByCalorieBurned() = runDao.getAllRunsSortedByCaloriesBured()

    fun getAllRunSortedByTimeInMillis() = runDao.getAllRunsSortedByTimeInMillis()

    fun getAllRunSortedByDistance() = runDao.getAllRunsSortedByDistance()

    fun getTotalDistance() = runDao.getTotalDistance()

    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()

    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()

    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()



}
