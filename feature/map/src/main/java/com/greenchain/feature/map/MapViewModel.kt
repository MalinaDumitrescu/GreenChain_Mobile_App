package com.greenchain.feature.map

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.greenchain.feature.map.data.RecyclingPointRepository
import com.greenchain.model.RecyclingPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NavigationInfo(val point: RecyclingPoint, val distance: Float)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: RecyclingPointRepository,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _recyclingPoints = MutableStateFlow<List<RecyclingPoint>>(emptyList())
    val recyclingPoints: StateFlow<List<RecyclingPoint>> = _recyclingPoints

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

    private val _navigationRequest = Channel<NavigationInfo>()
    val navigationRequest = _navigationRequest.receiveAsFlow()

    init {
        loadRecyclingPoints()
    }

    private fun loadRecyclingPoints() {
        viewModelScope.launch {
            _recyclingPoints.value = repository.getRecyclingPoints()
        }
    }

    fun findNearestRecyclingPoint() {
        val userLocation = _currentLocation.value ?: return
        val activePoints = _recyclingPoints.value.filter { it.isActive }
        if (activePoints.isEmpty()) return

        var nearestPoint: RecyclingPoint? = null
        var minDistance = -1f

        activePoints.forEach { point ->
            val distanceResults = FloatArray(1)
            Location.distanceBetween(
                userLocation.latitude, userLocation.longitude,
                point.latitude, point.longitude,
                distanceResults
            )
            val currentDistance = distanceResults[0]

            if (minDistance == -1f || currentDistance < minDistance) {
                minDistance = currentDistance
                nearestPoint = point
            }
        }

        nearestPoint?.let { point ->
            viewModelScope.launch {
                _navigationRequest.send(NavigationInfo(point, minDistance))
            }
        }
    }

    fun toggleRecyclingPointStatus(point: RecyclingPoint) {
        _recyclingPoints.update { list ->
            list.map {
                if (it.id == point.id) {
                    it.copy(isActive = !it.isActive)
                } else {
                    it
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                _currentLocation.value = LatLng(it.latitude, it.longitude)
            }
        }
    }
}
