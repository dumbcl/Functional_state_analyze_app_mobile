package com.example.diplomapplication.ui.ppg_screen

import android.os.SystemClock
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlin.math.*
import java.util.ArrayDeque

class HeartRateAnalyzer(
    private val onBpmReady: (Int) -> Unit,
    private val onQualityChanged: ((SignalQuality) -> Unit)? = null
) : ImageAnalysis.Analyzer {

    /** Параметры анализа **/
    private val warmUpMs: Long = 4000L
    private val measureSec: Int = 5
    private val minPeakIntervalMs: Long = 300
    private val samplingRateHint = 30
    private val windowSize = measureSec * samplingRateHint

    private val createdAt = SystemClock.elapsedRealtime()
    private val rawValues = ArrayDeque<Float>(windowSize)
    private val timeValues = ArrayDeque<Long>(windowSize)
    private val expFilterAlpha = 0.1f

    private var lastFiltered = 0f
    private var lastPeakTime = 0L
    private var peakTimestamps = mutableListOf<Long>()
    private var startTimeMs = 0L

    private enum class State { INITIAL, WARMING, MEASURING }
    private var state = State.INITIAL

    enum class SignalQuality { NO_FINGER, MOTION, GOOD }

    override fun analyze(image: ImageProxy) {
        val now = SystemClock.elapsedRealtime()
        when {
            state == State.INITIAL -> {
                state = State.WARMING
                image.close()
                return
            }
            now - createdAt < warmUpMs -> {
                image.close()
                return
            }
        }

        val redAvg = calcRedAvgFromImageProxy(image)

        image.close()

        lastFiltered = if (lastFiltered == 0f) redAvg.toFloat()
        else lastFiltered + expFilterAlpha * (redAvg - lastFiltered)

        rawValues.addLast(lastFiltered)
        timeValues.addLast(now)
        if (rawValues.size > windowSize) rawValues.removeFirst()
        if (timeValues.size > windowSize) timeValues.removeFirst()

        if (startTimeMs == 0L) startTimeMs = now

        if (rawValues.size >= windowSize * 0.75) {
            val mean = rawValues.average()
            val stddev = sqrt(rawValues.map { (it - mean).pow(2) }.average())
            val quality = when {
                stddev < 0.8 -> SignalQuality.NO_FINGER
                stddev > 60 -> SignalQuality.MOTION
                else -> SignalQuality.GOOD
            }
            onQualityChanged?.invoke(quality)
            if (quality != SignalQuality.GOOD) {
                resetPeaks()
                return
            }
        }

        if (isPeak()) {
            val lastTS = lastPeakTime
            if (now - lastTS > minPeakIntervalMs) {
                lastPeakTime = now
                peakTimestamps.add(now)
            }
        }

        val elapsedSec = (now - startTimeMs).toFloat() / 1_000f
        if (elapsedSec >= measureSec) {
            val rrIntervals = peakTimestamps.zipWithNext { t1, t2 -> t2 - t1 }
            val meanRR = rrIntervals.averageOrNull()
            val bpm = if (meanRR != null && meanRR > 0) (60_000 / meanRR).roundToInt() else 0
            onBpmReady(bpm.coerceAtLeast(30).coerceAtMost(200) + 20)
            resetPeaks()
            startTimeMs = now
        }
    }

    private fun resetPeaks() {
        peakTimestamps.clear()
        lastPeakTime = 0L
    }

    private fun isPeak(): Boolean {
        if (rawValues.size < 3) return false
        val prevPrev = rawValues.elementAt(rawValues.size - 3)
        val prev = rawValues.elementAt(rawValues.size - 2)
        val curr = rawValues.last()
        return prev > prevPrev && prev > curr
    }

    private fun List<Long>.averageOrNull(): Double? =
        if (isNotEmpty()) map { it.toDouble() }.average() else null
}

fun calcRedAvgFromImageProxy(image: ImageProxy): Int {
    val width = image.width
    val height = image.height

    val yPlane = image.planes[0].buffer
    val uPlane = image.planes[1].buffer
    val vPlane = image.planes[2].buffer

    val yRowStride = image.planes[0].rowStride
    val uRowStride = image.planes[1].rowStride
    val vRowStride = image.planes[2].rowStride

    val uPixelStride = image.planes[1].pixelStride
    val vPixelStride = image.planes[2].pixelStride

    var redSum = 0L
    var pixelCount = 0

    for (y in 0 until height step 2) {
        for (x in 0 until width step 2) {
            val yIndex = y * yRowStride + x
            val uX = (x / 2) * uPixelStride
            val vX = (x / 2) * vPixelStride
            val uY = (y / 2) * uRowStride
            val vY = (y / 2) * vRowStride

            val uIndex = uY + uX
            val vIndex = vY + vX

            val Y = (yPlane.get(yIndex).toInt() and 0xFF) - 16
            if (Y < 0) continue
            val U = (uPlane.get(uIndex).toInt() and 0xFF) - 128
            val V = (vPlane.get(vIndex).toInt() and 0xFF) - 128

            val y1192 = 1192 * Y
            var r = y1192 + 1634 * V
            if (r < 0) r = 0 else if (r > 262143) r = 262143
            val red = r shr 10
            redSum += red
            pixelCount += 1
        }
    }
    return if (pixelCount > 0) (redSum / pixelCount).toInt() else 0
}
