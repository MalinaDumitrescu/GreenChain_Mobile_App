package com.greenchain.feature.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val clujNapoca = LatLng(46.77, 23.62)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(clujNapoca, 12f)
    }

    val recyclingPoints by viewModel.recyclingPoints.collectAsState()
    val context = LocalContext.current

    var hasLocationPermission by remember { mutableStateOf(false) }
    var navigationInfo by remember { mutableStateOf<NavigationInfo?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) {
                viewModel.getCurrentLocation()
            }
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Listen for navigation requests and show dialog
    LaunchedEffect(Unit) {
        viewModel.navigationRequest.collect { info ->
            // Animate camera to the point
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(LatLng(info.point.latitude, info.point.longitude), 15f),
                durationMs = 1000
            )
            // Wait for animation to finish
            delay(1000)
            // Then show the dialog
            navigationInfo = info
        }
    }

    if (navigationInfo != null) {
        val point = navigationInfo!!.point
        val distance = navigationInfo!!.distance

        AlertDialog(
            onDismissRequest = { navigationInfo = null },
            title = { Text("Navigation") },
            text = { Text("The nearest recycling point is: ${point.name}.\nIt's approximately ${distance.toInt()} meters away.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val gmmIntentUri = Uri.parse("google.navigation:q=${point.latitude},${point.longitude}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                        navigationInfo = null
                    }
                ) {
                    Text("Get Directions", color = Color(0xFF006400)) // Dark Green
                }
            },
            dismissButton = {
                TextButton(onClick = { navigationInfo = null }) {
                    Text("Cancel", color = Color(0xFF006400)) // Dark Green
                }
            }
        )
    }

    val mapProperties = MapProperties(
        isMyLocationEnabled = hasLocationPermission
    )

    Scaffold {
        paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties
            ) {
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
                        onInfoWindowClick = {
                            viewModel.toggleRecyclingPointStatus(point)
                        }
                    )
                }
            }

            FloatingActionButton(
                onClick = { viewModel.findNearestRecyclingPoint() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 72.dp, end = 16.dp)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = "Find nearest recycling point")
            }
        }
    }
}
