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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
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
        repository.getRecyclingPoints()
            .onEach { _recyclingPoints.value = it }
            .launchIn(viewModelScope)
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
        viewModelScope.launch {
            val newStatus = !point.isActive
            repository.updatePointStatus(point.id, newStatus)
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

    fun seedRecyclingPoints() {
        viewModelScope.launch {
            // Only seed if the database is empty
            if (repository.getRecyclingPoints().first().isEmpty()) {
                val points = setOf(
                    46749500L to 23518900L,
                    46745200L to 23522200L,
                    46742400L to 23532000L,
                    46763600L to 23525900L,
                    46750600L to 23534200L,
                    46753400L to 23536500L,
                    46756100L to 23543900L,
                    46751800L to 23546300L,
                    46754200L to 23546300L,
                    46755500L to 23548000L,
                    46757900L to 23548400L,
                    46754200L to 23550800L,
                    46757900L to 23548400L,
                    46767600L to 23548800L,
                    46766500L to 23549500L,
                    46762000L to 23556000L,
                    46758100L to 23551300L,
                    46762000L to 23556000L,
                    46759400L to 23558300L,
                    46756600L to 23558500L,
                    46753900L to 23555800L,
                    46750000L to 23558000L,
                    46750600L to 23561800L,
                    46755000L to 23560300L,
                    46747700L to 23563700L,
                    46751900L to 23564800L,
                    46772200L to 23558300L,
                    46773400L to 23563900L,
                    46784400L to 23556200L,
                    46783600L to 23558300L,
                    46754700L to 23575700L,
                    46755800L to 23578200L,
                    46754800L to 23578100L,
                    46755300L to 23579300L,
                    46744600L to 23584900L,
                    46744000L to 23585400L,
                    46756000L to 23583200L,
                    46758600L to 23583400L,
                    46748700L to 23600300L,
                    46750500L to 23593100L,
                    46756800L to 23595200L,
                    46766300L to 23591400L,
                    46770100L to 23593400L,
                    46771700L to 23591100L,
                    46772200L to 23589200L,
                    46773000L to 23589800L,
                    46774800L to 23589300L,
                    46774200L to 23593100L,
                    46774100L to 23594000L,
                    46782600L to 23587700L,
                    46778300L to 23596700L,
                    46795300L to 23604100L,
                    46789900L to 23605600L,
                    46777600L to 23602200L,
                    46764800L to 23606600L,
                    46762000L to 23607100L,
                    46761300L to 23613800L,
                    46760400L to 23613500L,
                    46759400L to 23614400L,
                    46757800L to 23612900L,
                    46755800L to 23623000L,
                    46752600L to 23633300L,
                    46772800L to 23607300L,
                    46769000L to 23608700L,
                    46769400L to 23610000L,
                    46789900L to 23605600L,
                    46777500L to 23608200L,
                    46778000L to 23611400L,
                    46776500L to 23612200L,
                    46769300L to 23616000L,
                    46771000L to 23617300L,
                    46769200L to 23619500L,
                    46767300L to 23625000L,
                    46767800L to 23635500L,
                    46768200L to 23636700L,
                    46779900L to 23665000L,
                    46789200L to 23644100L,
                    46777400L to 23636200L,
                    46778900L to 23629700L,
                    46776100L to 23628200L,
                    46773100L to 23629300L,
                    46776000L to 23621100L,
                    46774800L to 23620000L,
                    46769200L to 23619500L,
                    46774300L to 23620500L,
                    46779700L to 23619900L,
                    46782000L to 23627400L,
                    46781500L to 23626000L,
                    46781100L to 23623200L,
                    46778000L to 23611400L,
                    46777500L to 23608200L,
                    46780100L to 23613100L,
                    46762700L to 23577900L,
                ).map { (lat, lon) ->
                    RecyclingPoint(
                        id = UUID.randomUUID().toString(),
                        name = "Recycling Point",
                        latitude = lat / 1000000.0,
                        longitude = lon / 1000000.0,
                        isActive = true
                    )
                }
                repository.seedPoints(points)
            }
        }
    }
}
