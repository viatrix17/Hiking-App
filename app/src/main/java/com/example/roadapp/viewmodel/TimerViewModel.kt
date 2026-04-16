package com.example.roadapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roadapp.model.Timer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    private val _timerStates = MutableStateFlow<Map<String, Timer>>(emptyMap())
    val timerStates : StateFlow<Map<String, Timer>> = _timerStates.asStateFlow()

    private val _activeRouteName = MutableStateFlow<String?>(null)
    val activeRouteName: StateFlow<String?> = _activeRouteName.asStateFlow()
    private var totalSeconds = 0
    private var timerJob: Job? = null

    fun startTimer(routeName: String) {
        if (_activeRouteName.value != null && _activeRouteName.value != routeName) return
        if(timerJob?.isActive == true) return

        _activeRouteName.value = routeName

        timerJob = viewModelScope.launch() {

            while (isActive) {
                delay(1000L)

                val currentMap = _timerStates.value
                val currentTimer = currentMap[routeName] ?: Timer(routeName = routeName)

                totalSeconds++
                val h = totalSeconds / 3600
                val m = (totalSeconds % 3600) / 60
                val s = totalSeconds % 60

                val updatedTimer = Timer(seconds = s, minutes = m, hours = h, isRunning = true)
                _timerStates.value = currentMap.toMutableMap().apply {
                    put(routeName, updatedTimer)


            }
            }
        }
    }

    fun stopTimer(routeName: String) {
        timerJob?.cancel()
        timerJob = null

        _activeRouteName.value = null

        val currentMap = _timerStates.value.toMutableMap()
        currentMap[routeName]?.let {
            currentMap[routeName] = it.copy(isRunning = false)
        }
        _timerStates.value = currentMap
    }

    fun resetTimer(routeName: String) {
        stopTimer(routeName)

        totalSeconds = 0

        val currentMap = _timerStates.value.toMutableMap()
        currentMap[routeName] = Timer(routeName = routeName, seconds = 0, minutes = 0, hours = 0, isRunning = false)
        _timerStates.value = currentMap
    }

    fun saveTime() { //baza danych

    }
}
