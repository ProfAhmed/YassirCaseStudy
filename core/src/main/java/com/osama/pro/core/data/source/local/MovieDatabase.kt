package com.osama.pro.core.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.osama.pro.core.data.source.local.dao.MovieDao
import com.osama.pro.core.data.source.local.entity.MovieEntity

@Database(
    entities = [
        MovieEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}