package com.greenchain.feature.map.data

import android.content.Context
import com.greenchain.model.RecyclingPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject

class RecyclingPointDataSource @Inject constructor(
    private val context: Context
) {

    suspend fun getRecyclingPoints(): List<RecyclingPoint> = withContext(Dispatchers.IO) {
        val jsonString = context.assets.open("recycling_points.json").bufferedReader().use { it.readText() }
        val featureCollection = JSONObject(jsonString)
        val features = featureCollection.getJSONArray("features")
        val points = mutableListOf<RecyclingPoint>()

        for (i in 0 until features.length()) {
            val feature = features.getJSONObject(i)
            val geometry = feature.getJSONObject("geometry")
            if (geometry.getString("type") == "Point") {
                val coordinates = geometry.getJSONArray("coordinates")
                val longitude = coordinates.getDouble(0)
                val latitude = coordinates.getDouble(1)

                val properties = feature.optJSONObject("properties")
                val name = properties?.optString("name", "$latitude, $longitude") ?: "$latitude, $longitude"

                points.add(
                    RecyclingPoint(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        longitude = longitude,
                        latitude = latitude
                    )
                )
            }
        }
        points
    }
}
