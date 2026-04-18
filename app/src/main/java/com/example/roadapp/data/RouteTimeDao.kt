package com.example.roadapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteTimeDao {
    @Query("SELECT * FROM routeTime")
    fun getAllRouteTimes(): LiveData<List<RouteTime>>
    @Query(value = "SELECT * FROM routeTime WHERE routeName = :name ORDER BY timestamp DESC")
    fun getRouteTimesByName(name: String): Flow<List<RouteTime>>
    @Insert
    suspend fun insertRouteTime(routeTime: RouteTime)
    @Update
    suspend fun updateRouteTime(routeTime: RouteTime)
    @Delete
    suspend fun deleteRouteTime(routeTime: RouteTime)
}