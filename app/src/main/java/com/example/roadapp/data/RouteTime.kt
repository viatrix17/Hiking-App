package com.example.roadapp.data
import androidx.room.*

@Entity
data class RouteTime(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // unikalne ID (auto-generowane)
    val routeName: String,
    val durationInMillis: Long,
    val timestamp: Long
)