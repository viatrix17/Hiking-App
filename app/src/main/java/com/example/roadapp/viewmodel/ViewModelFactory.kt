package com.example.roadapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roadapp.data.RouteTimeDao

class TimerViewModelFactory(private val dao: RouteTimeDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}