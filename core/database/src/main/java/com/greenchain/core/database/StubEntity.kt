package com.greenchain.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stub_entities")
data class StubEntity(
    @PrimaryKey val id: Int = 1, // single row cache
    val message: String
)
