package com.example.diplomapplication.ui.ppg_screen

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class HeartRateAnalyzer(
    private val updateHeartRate: (Int) -> Unit,
) : ImageAnalysis.Analyzer {
    private val processing = AtomicBoolean(false)
    private val isStarted = AtomicBoolean(false)

    private val averageArraySize = 4
    private val averageArray = IntArray(averageArraySize)
    private var averageIndex = 0

    private val beatsArraySize = 3
    private val beatsArray = IntArray(beatsArraySize)
    private var beatsIndex = 0

    private var startTime = 0L
    private var beats = 0

    private enum class TYPE { RED, GREEN }
    private var currentType = TYPE.GREEN

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun analyze(image: ImageProxy) {
        if (isStarted.get().not()) {
            scope.launch {
                delay(5000)
                isStarted.set(true)
            }
        }
        if (!processing.compareAndSet(false, true)) {
            image.close()
            return
        }

        val nv21 = imageProxyToNV21(image)
        val width = image.cropRect.width()
        val height = image.cropRect.height()
        val imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(nv21, width, height)
        if (imgAvg == 0 || imgAvg == 255) {
            processing.set(false)
            image.close()
            return
        }

        var averageArrayAvg = 0
        var averageArrayCnt = 0
        for (value in averageArray) {
            if (value > 0) {
                averageArrayAvg += value
                averageArrayCnt++
            }
        }
        val rollingAverage = if (averageArrayCnt > 0) averageArrayAvg / averageArrayCnt else 0

        var newType = currentType
        if (imgAvg < rollingAverage) {
            newType = TYPE.RED
            if (newType != currentType) {
                beats++
            }
        } else if (imgAvg > rollingAverage) {
            newType = TYPE.GREEN
        }

        averageArray[averageIndex] = imgAvg
        averageIndex = (averageIndex + 1) % averageArraySize

        if (newType != currentType) {
            currentType = newType
        }

        if (startTime == 0L) startTime = System.currentTimeMillis()

        val endTime = System.currentTimeMillis()
        val totalTimeInSecs = (endTime - startTime) / 1000.0
        if (totalTimeInSecs >= 5) {
            val bps = beats / totalTimeInSecs
            val dpm = (bps * 60.0).toInt()

            if (dpm in 30..180) {
                beatsArray[beatsIndex] = dpm
                beatsIndex = (beatsIndex + 1) % beatsArraySize

                var beatsArrayAvg = 0
                var beatsArrayCnt2 = 0
                for (b in beatsArray) {
                    if (b > 0) {
                        beatsArrayAvg += b
                        beatsArrayCnt2++
                    }
                }
                val beatsAvg = if (beatsArrayCnt2 > 0) beatsArrayAvg / beatsArrayCnt2 else dpm

                updateHeartRate.invoke(beatsAvg)
            }
            startTime = System.currentTimeMillis()
            beats = 0
        }

        processing.set(false)
        image.close()
    }

    private fun imageProxyToNV21(image: ImageProxy): ByteArray {
        val cropWidth = image.cropRect.width()
        val cropHeight = image.cropRect.height()
        val yBuffer = image.planes[0].buffer // Y
        val uBuffer = image.planes[1].buffer // U
        val vBuffer = image.planes[2].buffer // V

        val nv21ByteArray = ByteArray(cropWidth * cropHeight * 3 / 2)

        yBuffer.position(image.planes[0].buffer.position())
        yBuffer.get(nv21ByteArray, 0, cropWidth * cropHeight)

        val uRowStride = image.planes[1].rowStride
        val vRowStride = image.planes[2].rowStride
        val uPixelStride = image.planes[1].pixelStride
        val vPixelStride = image.planes[2].pixelStride

        var uvIndex = cropWidth * cropHeight

        for (row in 0 until cropHeight / 2) {
            var uRowPos = row * uRowStride
            var vRowPos = row * vRowStride

            for (col in 0 until cropWidth / 2) {
                val vValue = vBuffer.get(vRowPos).toInt() and 0xFF
                val uValue = uBuffer.get(uRowPos).toInt() and 0xFF

                nv21ByteArray[uvIndex++] = vValue.toByte()
                nv21ByteArray[uvIndex++] = uValue.toByte()

                vRowPos += vPixelStride
                uRowPos += uPixelStride
            }
        }
        return nv21ByteArray
    }

}
