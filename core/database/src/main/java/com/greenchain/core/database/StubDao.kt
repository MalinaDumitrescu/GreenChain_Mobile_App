package com.greenchain.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StubDao {
    @Query("SELECT * FROM stub_entities WHERE id = 1")
    suspend fun get(): StubEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(e: StubEntity)
}
