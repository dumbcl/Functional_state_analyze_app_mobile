package com.example.diplomapplication.ui.ppg_screen

import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    updateHeartRate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    val analyzerExecutor = remember { Executors.newSingleThreadExecutor() }
    AndroidView(
        modifier = modifier,
        factory = { previewView }
    ) { view ->
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val previewUseCase = Preview.Builder().build().also {
                it.surfaceProvider = view.surfaceProvider
            }
            val analysisUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            val analyzer = HeartRateAnalyzer(updateHeartRate)
            analysisUseCase.setAnalyzer(analyzerExecutor, analyzer)

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            cameraProvider.unbindAll()

            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                previewUseCase,
                analysisUseCase,
            )
        }, ContextCompat.getMainExecutor(context))
    }
}
