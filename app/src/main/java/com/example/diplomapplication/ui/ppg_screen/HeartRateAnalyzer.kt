package com.example.diplomapplication.ui.ppg_screen

import android.os.SystemClock
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.lang.Math.random
import kotlin.math.*
import java.util.ArrayDeque
import java.util.LinkedList

class HeartRateAnalyzer(
    private val onBpmReady: (Int) -> Unit,
    private val onQualityChanged: ((SignalQuality) -> Unit)? = null
) : ImageAnalysis.Analyzer {

    // === SETTINGS ===
    private val warmUpMs: Long = 3500L   // warming before start analyzing
    private val measureWindowSec: Int = 5
    private val sessionDurationSec: Int = 30
    private val minPeakIntervalMs: Long = 350L // physiological (170 bpm)
    private val samplingRateHint = 30 // frames per second
    private val windowSize = measureWindowSec * samplingRateHint

    private val createdAt = SystemClock.elapsedRealtime()
    private val rawValues = ArrayDeque<Float>(windowSize)
    private val timeValues = ArrayDeque<Long>(windowSize)
    private val filterWindow = 5 // MA filter width
    private val trendWindow = 15 // trend removal window (longer)
    private val bpmHistory = LinkedList<Int>()

    private var lastFiltered = 0f
    private var lastPeakTime = 0L
    private var peakTimestamps = mutableListOf<Long>()
    private var startTimeMs = 0L
    private var totalMeasurementStart = 0L

    private enum class State { INITIAL, WARMING, MEASURING }
    private var state = State.INITIAL

    enum class SignalQuality { NO_FINGER, MOTION, GOOD }

    override fun analyze(image: ImageProxy) {
        val now = SystemClock.elapsedRealtime()

        when {
            state == State.INITIAL -> {
                state = State.WARMING
                totalMeasurementStart = now
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

        // Детрендирование: скользящее среднее как тренд
        val trend = if (rawValues.size >= trendWindow)
            rawValues.takeLast(trendWindow).average().toFloat()
        else
            0f

        val detrended = redAvg - trend

        // Скользящее среднее как примитивная полосовая фильтрация
        rawValues.addLast(detrended)
        timeValues.addLast(now)

        if (rawValues.size > windowSize) rawValues.removeFirst()
        if (timeValues.size > windowSize) timeValues.removeFirst()

        val filtered = if (rawValues.size >= filterWindow) {
            rawValues.takeLast(filterWindow).average().toFloat()
        } else detrended

        lastFiltered = filtered

        if (startTimeMs == 0L) {
            startTimeMs = now
        }

        // Качество сигнала
        if (rawValues.size >= windowSize * 0.75) {
            val mean = rawValues.average()
            val stddev = sqrt(rawValues.map { (it - mean).pow(2) }.average())
            val quality = when {
                stddev < 1.2 -> SignalQuality.NO_FINGER
                stddev > 60 -> SignalQuality.MOTION
                else -> SignalQuality.GOOD
            }
            onQualityChanged?.invoke(quality)
            if (quality != SignalQuality.GOOD) {
                resetPeaks()
                return
            }
        }

        // Улучшенный пик-детектор через разностное окно и амплитудный порог
        if (rawValues.size >= 3) {
            val prev2 = rawValues.elementAt(rawValues.size - 3)
            val prev1 = rawValues.elementAt(rawValues.size - 2)
            val curr  = rawValues.last()
            val mean = rawValues.average()
            val ampThreshold = 2.5f // можно тюнить

            if (prev1 > prev2 && prev1 > curr && prev1 - mean > ampThreshold) {
                if (now - lastPeakTime > minPeakIntervalMs) {
                    lastPeakTime = now
                    peakTimestamps.add(now)
                }
            }
        }

        // Каждые 5с пересчитываем BPM
        val elapsedSec = (now - startTimeMs) / 1000f
        if (elapsedSec >= measureWindowSec) {
            val rrIntervals = peakTimestamps.zipWithNext { t1, t2 -> t2 - t1 }
                .filter { it in 350..2000 }
            val meanRR = rrIntervals.averageOrNull()
            val bpm = if (meanRR != null && meanRR > 0) (60_000 / meanRR).roundToInt() else 0
            onBpmReady(0)

            if (bpm in 40..200) bpmHistory.addLast(bpm)
            if (bpmHistory.size > sessionDurationSec / measureWindowSec)
                bpmHistory.removeFirst()

            // расчет медианы по истории
            val medianBpm = bpmHistory.sorted().let {
                if (it.isNotEmpty()) it[it.size / 2] else 0
            }

            if (bpm in 40..200) onBpmReady(medianBpm)

            resetPeaks()
            startTimeMs = now

            // Для 30-40 секунд работы авто-сброс
            if (now - totalMeasurementStart > sessionDurationSec * 1000L) {
                bpmHistory.clear()
                startTimeMs = 0L
            }
        }
    }

    private fun resetPeaks() {
        peakTimestamps.clear()
        lastPeakTime = 0L
    }

    private fun List<Long>.averageOrNull(): Double? =
        if (isNotEmpty()) map { it.toDouble() }.average() else null
}

// --- calcRedAvgFromImageProxy.kt
fun calcRedAvgFromImageProxy(image: ImageProxy): Int {
    // Точно так же, как у вас в оригинальном коде
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

fun <T> ArrayDeque<T>.takeLast(n: Int): List<T> {
    if (n <= 0) return emptyList()
    return this.toList().takeLast(n).toList()
}
