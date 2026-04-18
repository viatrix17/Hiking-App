package com.example.roadapp.model

data class RouteHistoryRecord(
    val timer: Timer,
    val formattedDate: String,
    val rawTimestamp: Long
)