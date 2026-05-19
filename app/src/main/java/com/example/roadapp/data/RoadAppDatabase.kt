package com.example.roadapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [RouteTime::class], version = 1) // Zmień na 2, gdy dodasz migrację
abstract class RoadAppDatabase : RoomDatabase() {
    abstract fun routeTimeDao(): RouteTimeDao

    companion object {
        @Volatile
        private var INSTANCE: RoadAppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }

        fun getInstance(context: Context): RoadAppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RoadAppDatabase::class.java,
                    "road_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}