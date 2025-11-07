package com.greenchain.feature.scan.ui

import android.Manifest
import android.content.ContentValues
import android.graphics.Bitmap
import android.media.MediaActionSound
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.greenchain.feature.scan.util.cropBitmapByRelativeRect
import com.greenchain.feature.scan.util.loadBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScanScreen(
    onCaptured: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // ---- Framing square (smaller) ----
    val squareSize = 0.4f // 40% of the view; make smaller like 0.35f or 0.3f if you want tighter
    val relLeft = (1f - squareSize) / 2f
    val relTop = (1f - squareSize) / 2f
    val relRight = 1f - relLeft
    val relBottom = 1f - relTop

    // Permissions
    var hasCameraPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasCameraPermission = isGranted }
    )
    LaunchedEffect(Unit) { permissionLauncher.launch(Manifest.permission.CAMERA) }

    // CameraX state
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var hasCamera by remember { mutableStateOf(false) }

    // Result preview (cropped)
    var croppedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Capture state (prevents double taps)
    var isCapturing by remember { mutableStateOf(false) }

    // Shutter feedback: flash + haptic + sound
    val haptics = LocalHapticFeedback.current
    var flashOn by remember { mutableStateOf(false) }
    val shutterSound = remember { MediaActionSound().apply { load(MediaActionSound.SHUTTER_CLICK) } }
    DisposableEffect(Unit) {
        onDispose { shutterSound.release() }
    }
    fun triggerShutterFeedback() {
        flashOn = true
        shutterSound.play(MediaActionSound.SHUTTER_CLICK)
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        // Auto-hide the flash quickly
        scope.launch {
            delay(120)
            flashOn = false
        }
    }

    Box(modifier.fillMaxSize()) {
        when {
            // Show ONLY the cropped square after capture
            croppedBitmap != null -> {
                Image(
                    bitmap = croppedBitmap!!.asImageBitmap(),
                    contentDescription = "Cropped logo",
                    modifier = Modifier.fillMaxSize()
                )
                Row(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = { croppedBitmap = null }) { Text("Retake") }
                }
            }

            // Camera preview + overlay
            hasCameraPermission -> {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            // Keep full image visible (letterboxed if needed) so overlay maps to saved bitmap
                            scaleType = PreviewView.ScaleType.FIT_CENTER
                        }
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            imageCapture = ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                .build()

                            val selector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner, selector, preview, imageCapture
                                )
                                hasCamera = true
                            } catch (_: Exception) {
                                hasCamera = false
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    }
                )

                // Visible framing square on top
                FramingOverlay(relLeft, relTop, relRight, relBottom)
            }

            // Permission request UI
            else -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Camera permission is required to scan.")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Grant Permission")
                    }
                }
            }
        }

        // Capture button (only when showing camera)
        if (hasCameraPermission && hasCamera && croppedBitmap == null) {
            FloatingActionButton(
                onClick = {
                    if (isCapturing) return@FloatingActionButton
                    val imgCap = imageCapture ?: return@FloatingActionButton

                    isCapturing = true
                    triggerShutterFeedback()

                    val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, "GC_$name.jpg")
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/GreenChain")
                    }
                    val resolver = context.contentResolver
                    val outputOptions = ImageCapture.OutputFileOptions
                        .Builder(
                            resolver,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        ).build()

                    imgCap.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onError(exc: ImageCaptureException) {
                                isCapturing = false
                                // TODO: Show snackbar/toast if you want
                            }
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                isCapturing = false
                                output.savedUri?.let { uri ->
                                    // Still propagate original URI if other flows need it
                                    onCaptured(uri)
                                    // Crop the saved image to the visible square
                                    scope.launch {
                                        val bmp = loadBitmap(context, uri)
                                        croppedBitmap = cropBitmapByRelativeRect(
                                            bmp, relLeft, relTop, relRight, relBottom
                                        )
                                    }
                                }
                            }
                        }
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp)
            ) {
                if (isCapturing) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(6.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("Scan")
                }
            }
        }

        // Quick white flash overlay when capturing
        if (flashOn) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.85f))
            )
        }
    }
}
