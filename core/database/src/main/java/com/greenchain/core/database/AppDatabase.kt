package com.greenchain.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [StubEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stubDao(): StubDao
}
