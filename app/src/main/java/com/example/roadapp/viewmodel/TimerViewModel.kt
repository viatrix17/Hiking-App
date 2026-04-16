package com.example.roadapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roadapp.model.Timer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    private val _timerState = MutableStateFlow(Timer())
    val timerState : StateFlow<Timer> = _timerState.asStateFlow()

    private var totalSeconds = 0
    private var timerJob: Job? = null

    fun startTimer() {
        if(timerJob?.isActive == true) return

        _timerState.value = _timerState.value.copy(isRunning = true)
        timerJob = viewModelScope.launch() {
            while (_timerState.value.isRunning) {
                totalSeconds++

                val h = totalSeconds / 3600
                val m = (totalSeconds % 3600) / 60
                val s = totalSeconds % 60

                _timerState.value = Timer(seconds = s, minutes = m, hours = h, isRunning = true)

                delay(1000L)
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null

        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    fun resetTimer() {
        _timerState.value = _timerState.value.copy(seconds = 0, minutes = 0, hours = 0, isRunning = false)
        totalSeconds = 0
    }

    fun saveTime() { //baza danych

    }
}
