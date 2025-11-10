package com.greenchain.feature.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val clujNapoca = LatLng(46.77, 23.62)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(clujNapoca, 12f)
    }

    val recyclingPoints by viewModel.recyclingPoints.collectAsState()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Reverting to the stable, non-clustering implementation.
        recyclingPoints.forEach { point ->
            val icon = if (point.isActive) {
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            } else {
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            }

            Marker(
                state = rememberMarkerState(key = point.id, position = LatLng(point.latitude, point.longitude)),
                title = point.name,
                snippet = if (point.isActive) "Status: Active. Click here to deactivate." else "Status: Inactive. Click here to activate.",
                icon = icon,
                // Using onInfoWindowClick is a standard, stable way to handle interactions.
                onInfoWindowClick = {
                    viewModel.toggleRecyclingPointStatus(point)
                }
            )
        }
    }
}
