package com.greenchain.feature.map.data

import com.greenchain.model.RecyclingPoint
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecyclingPointRepository @Inject constructor(
    private val dataSource: RecyclingPointDataSource
) {
    suspend fun getRecyclingPoints(): List<RecyclingPoint> {
        return dataSource.getRecyclingPoints()
    }
}
