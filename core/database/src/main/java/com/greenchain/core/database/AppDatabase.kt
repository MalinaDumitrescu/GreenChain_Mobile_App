package com.greenchain.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

import com.greenchain.core.database.dao.UserDao
import com.greenchain.core.database.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}
