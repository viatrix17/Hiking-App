package com.example.roadapp.data

import com.example.roadapp.model.RouteHistoryRecord
import com.example.roadapp.model.Timer
import com.example.roadapp.util.formatTimestamp

fun Timer.toEntity(): RouteTime {
    android.util.Log.d("DEBUG_ENTITY", "Mapuję timer dla trasy: '$routeName'")

    val totalMillis = (hours.toLong() * 3_600_000L) +
            (minutes.toLong() * 60_000L) +
            (seconds.toLong() * 1_000L)

    return RouteTime(
        routeName = this.routeName,
        durationInMillis = totalMillis,
        timestamp = System.currentTimeMillis()
    )
}

fun RouteTime.toTimer(): Timer {
    val totalSeconds = durationInMillis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return Timer(
        routeName = this.routeName,
        hours = hours.toInt(),
        minutes = minutes.toInt(),
        seconds = seconds.toInt(),
    )
}

fun RouteTime.toUiModel(): RouteHistoryRecord {
    return RouteHistoryRecord(
        timer = this.toTimer(),
        formattedDate = formatTimestamp(this.timestamp),
        rawTimestamp = this.timestamp
    )
}