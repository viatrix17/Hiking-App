package com.example.roadapp.model

data class Timer (
    val routeName: String = "",
    val seconds: Int = 0,
    val minutes: Int = 0,
    val hours: Int = 0,
    val isRunning: Boolean = false
)