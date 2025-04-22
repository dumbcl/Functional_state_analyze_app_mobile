package com.example.diplomapplication.ui.ppg_screen

import android.hardware.camera2.CameraMetadata
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import java.util.concurrent.Executors

@OptIn(ExperimentalCamera2Interop::class)
@Composable
fun CameraPreview(
    updateHeartRate: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProvider = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val analyzerExecutor = remember { Executors.newSingleThreadExecutor() }
    val analyzer = remember { HeartRateAnalyzer(updateHeartRate) }

    AndroidView(
        factory  = { previewView },
        modifier = modifier,
    )

    DisposableEffect(Unit) {
        val provider = cameraProvider.get()

        val preview = Preview.Builder()
            .setTargetResolution(Size(320, 240))
            .setTargetRotation(previewView.display.rotation)
            .also { Camera2Interop.Extender(it)
                .setCaptureRequestOption(android.hardware.camera2.CaptureRequest.FLASH_MODE,
                    CameraMetadata.FLASH_MODE_TORCH) }
            .build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

        val analysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(320, 240))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().also { it.setAnalyzer(analyzerExecutor, analyzer) }

        val camera = provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            analysis
        )
        camera.cameraControl.enableTorch(true)

        onDispose {
            provider.unbindAll()
            analyzerExecutor.shutdown()
        }
    }
}

