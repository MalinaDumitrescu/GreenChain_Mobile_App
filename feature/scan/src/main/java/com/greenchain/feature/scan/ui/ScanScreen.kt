package com.greenchain.feature.scan.ui

import android.Manifest
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.greenchain.feature.scan.util.loadBitmap
import com.greenchain.feature.scan.util.cropBitmapByRelativeRect
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

    // ---- Square we want the user to frame (center 70% of width, keep it a square) ----
    val squareSize = 0.4f                      // 60% of the view
    val relLeft = (1f - squareSize) / 2f
    val relTop = (1f - squareSize) / 2f
    val relRight = 1f - relLeft
    val relBottom = 1f - relTop

    var hasCameraPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasCameraPermission = isGranted }
    )
    LaunchedEffect(Unit) { permissionLauncher.launch(Manifest.permission.CAMERA) }

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var hasCamera by remember { mutableStateOf(false) }
    var croppedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Box(modifier.fillMaxSize()) {

        if (croppedBitmap != null) {
            // Show ONLY the cropped square after capture
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
                // Keep your existing onCaptured(uri) flow if you also need original URI
            }
        } else if (hasCameraPermission) {
            // Camera preview
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        // Use FIT_CENTER so the preview is not cropped;
                        // overlay rect then lines up proportionally with the saved image.
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

            // The visible square guide on top of the camera
            FramingOverlay(relLeft, relTop, relRight, relBottom)
        } else {
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

        // Capture button (only when showing camera)
        if (hasCameraPermission && hasCamera && croppedBitmap == null) {
            FloatingActionButton(
                onClick = {
                    val imgCap = imageCapture ?: return@FloatingActionButton
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
                                // TODO: snackbar/toast
                            }
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                output.savedUri?.let { uri ->
                                    // If you still need the original file elsewhere:
                                    onCaptured(uri)

                                    // Load full image and crop to the framing square
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
            ) { Text("Scan") }
        }
    }
}
