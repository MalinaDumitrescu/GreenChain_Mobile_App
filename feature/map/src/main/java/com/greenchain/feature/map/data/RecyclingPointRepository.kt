package com.greenchain.feature.map.data

import com.greenchain.model.RecyclingPoint
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecyclingPointRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val recyclingPointsCollection = firestore.collection("recycling_points")

    fun getRecyclingPoints(): Flow<List<RecyclingPoint>> {
        return recyclingPointsCollection.snapshots().map { snapshot ->
            snapshot.toObjects<RecyclingPointDto>().map { dto ->
                RecyclingPoint(
                    id = dto.id,
                    name = dto.name,
                    latitude = dto.latitude,
                    longitude = dto.longitude,
                    isActive = dto.isActive
                )
            }
        }
    }

    suspend fun updatePointStatus(pointId: String, newStatus: Boolean) {
        // This is the FINAL fix. It now correctly updates the "active" field in Firestore.
        recyclingPointsCollection.document(pointId).update("active", newStatus).await()
    }

    suspend fun seedPoints(points: List<RecyclingPoint>) {
        val batch = firestore.batch()
        points.forEach { point ->
            val dto = RecyclingPointDto(
                id = point.id,
                name = point.name,
                latitude = point.latitude,
                longitude = point.longitude,
                isActive = point.isActive
            )
            val docRef = recyclingPointsCollection.document(point.id)
            batch.set(docRef, dto)
        }
        batch.commit().await()
    }
}
